package si.daljinec

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.Executors

/** Opis naprave: tipkovnica (poročilo 1) in daljinec za medije (poročilo 2). */
private val HID_DESCRIPTOR = intArrayOf(
    // tipkovnica
    0x05, 0x01, 0x09, 0x06, 0xA1, 0x01, 0x85, 0x01,
    0x05, 0x07, 0x19, 0xE0, 0x29, 0xE7, 0x15, 0x00, 0x25, 0x01, 0x75, 0x01, 0x95, 0x08, 0x81, 0x02,
    0x95, 0x01, 0x75, 0x08, 0x81, 0x01,
    0x95, 0x05, 0x75, 0x01, 0x05, 0x08, 0x19, 0x01, 0x29, 0x05, 0x91, 0x02,
    0x95, 0x01, 0x75, 0x03, 0x91, 0x01,
    0x95, 0x06, 0x75, 0x08, 0x15, 0x00, 0x25, 0x65, 0x05, 0x07, 0x19, 0x00, 0x29, 0x65, 0x81, 0x00,
    0xC0,
    // daljinec (consumer control)
    0x05, 0x0C, 0x09, 0x01, 0xA1, 0x01, 0x85, 0x02,
    0x15, 0x00, 0x26, 0xFF, 0x03, 0x19, 0x00, 0x2A, 0xFF, 0x03, 0x75, 0x10, 0x95, 0x01, 0x81, 0x00,
    0xC0
).map { it.toByte() }.toByteArray()

/**
 * Bluetooth daljinec (HID). Živi neodvisno od zaslona, da ostane registriran,
 * tudi ko greš v nastavitve telefona (Android sicer registracijo prekliče).
 */
@SuppressLint("MissingPermission")
object BtHid {
    val log = mutableStateListOf<String>()
    var status by mutableStateOf("Ni povezano")
    val bonded = mutableStateListOf<BluetoothDevice>()

    private var app: Context? = null
    private var hid: BluetoothHidDevice? = null
    private var host: BluetoothDevice? = null
    private var wantedHost: BluetoothDevice? = null
    private var retries = 0
    private var started = false
    private val cbExec = Executors.newSingleThreadExecutor()
    private val sendExec = Executors.newSingleThreadExecutor()
    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    private val timeFmt = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US)

    private val adapter: BluetoothAdapter?
        get() = (app?.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    fun add(s: String) {
        val line = timeFmt.format(java.util.Date()) + "  " + s
        main.post {
            log.add(0, line)
            if (log.size > 40) log.removeAt(log.size - 1)
        }
    }

    private fun setStatus(s: String) = main.post { status = s }

    fun name(d: BluetoothDevice): String = try { d.name ?: d.address } catch (_: Exception) { d.address }

    private val callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            add(if (registered) "Registriran kot Bluetooth daljinec" else "Registracija preklicana")
            if (registered && pluggedDevice != null) hid?.connect(pluggedDevice)
        }

        override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    host = device
                    setStatus("Povezano: ${name(device)}")
                    add("Povezano: ${name(device)}")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (host == device) host = null
                    setStatus("Ni povezano")
                    add("Prekinjeno: ${name(device)}")
                    if (device == wantedHost && retries < 2) {
                        retries++
                        Thread.sleep(1500)
                        add("Ponovni poskus $retries od 2…")
                        hid?.connect(device)
                    }
                }
                BluetoothProfile.STATE_CONNECTING -> add("Povezujem: ${name(device)}…")
            }
        }

        override fun onGetReport(device: BluetoothDevice, type: Byte, id: Byte, bufferSize: Int) {
            add("Box zahteva poročilo (tip $type, id $id)")
            hid?.replyReport(device, type, id, ByteArray(if (id.toInt() == 2) 2 else 8))
        }

        override fun onSetReport(device: BluetoothDevice, type: Byte, id: Byte, data: ByteArray) {
            add("Box pošilja poročilo (tip $type, id $id)")
            hid?.reportError(device, BluetoothHidDevice.ERROR_RSP_SUCCESS)
        }

        override fun onSetProtocol(device: BluetoothDevice, protocol: Byte) {
            add("Box nastavlja protokol: $protocol")
        }

        override fun onInterruptData(device: BluetoothDevice, reportId: Byte, data: ByteArray) {
            add("Box pošilja podatke (id $reportId)")
        }

        override fun onVirtualCableUnplug(device: BluetoothDevice) {
            add("Box je odklopil daljinec (virtual cable unplug)")
        }
    }

    fun start(ctx: Context) {
        app = ctx.applicationContext
        refreshBonded()
        if (started) return
        val a = adapter
        if (a == null) { add("Telefon nima Bluetootha"); return }
        if (!a.isEnabled) { add("Vklopi Bluetooth na telefonu in odpri preizkus znova"); return }
        try {
            val ok = a.getProfileProxy(app, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    hid = proxy as BluetoothHidDevice
                    register()
                }

                override fun onServiceDisconnected(profile: Int) {
                    hid = null
                    started = false
                    add("Bluetooth storitev prekinjena")
                }
            }, BluetoothProfile.HID_DEVICE)
            started = ok
            if (!ok) add("Telefon ne podpira Bluetooth daljinca (HID)")
        } catch (e: Exception) {
            add("Napaka: ${e.message}")
        }
    }

    fun register() {
        val h = hid ?: run { add("Bluetooth storitev še ni pripravljena"); return }
        val sdp = BluetoothHidDeviceAppSdpSettings(
            "Daljinec", "Daljinec remote", "Daljinec",
            BluetoothHidDevice.SUBCLASS1_COMBO, HID_DESCRIPTOR
        )
        val reg = h.registerApp(sdp, null, null, cbExec, callback)
        add(if (reg) "Registracija poslana…" else "Registracija ni uspela (morda je že registriran)")
    }

    fun refreshBonded() {
        main.post {
            bonded.clear()
            try { adapter?.bondedDevices?.let { bonded.addAll(it) } } catch (_: Exception) {}
        }
    }

    fun connectTo(d: BluetoothDevice) {
        val h = hid
        if (h == null) { add("Bluetooth daljinec še ni pripravljen"); return }
        wantedHost = d
        retries = 0
        add(if (h.connect(d)) "Povezujem z ${name(d)}…" else "Povezava z ${name(d)} ni uspela")
    }

    /** Tipka daljinca (consumer usage), npr. 0x9C = program gor. */
    fun consumer(usage: Int, label: String) {
        val h = hid
        val d = host
        if (h == null || d == null) { add("Ni povezave z boxom"); return }
        sendExec.execute {
            val ok = h.sendReport(d, 2, byteArrayOf((usage and 0xFF).toByte(), (usage shr 8).toByte()))
            Thread.sleep(60)
            h.sendReport(d, 2, byteArrayOf(0, 0))
            add("Poslano: $label (${if (ok) "ok" else "napaka"})")
        }
    }

    /** Tipka tipkovnice (HID usage), npr. 0x04 = A. */
    fun keyboard(usage: Int, label: String) {
        val h = hid
        val d = host
        if (h == null || d == null) { add("Ni povezave z boxom"); return }
        sendExec.execute {
            val ok = h.sendReport(d, 1, byteArrayOf(0, 0, usage.toByte(), 0, 0, 0, 0, 0))
            Thread.sleep(60)
            h.sendReport(d, 1, ByteArray(8))
            add("Poslano: $label (${if (ok) "ok" else "napaka"})")
        }
    }

    fun stop() {
        try {
            hid?.unregisterApp()
            adapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hid)
        } catch (_: Exception) {}
        hid = null
        host = null
        started = false
        setStatus("Ni povezano")
        add("Ustavljeno")
    }
}

/** Storitev v ospredju: drži aplikacijo "budno", da Android ne prekliče Bluetooth daljinca. */
class BtKeepService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel("bt", "Bluetooth daljinec", NotificationManager.IMPORTANCE_LOW)
        )
        val n = Notification.Builder(this, "bt")
            .setContentTitle("Daljinec")
            .setContentText("Bluetooth daljinec je aktiven")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
        } else {
            startForeground(1, n)
        }
        BtHid.start(this)
        return START_STICKY
    }
}

/** Preizkus: telefon kot Bluetooth daljinec za box. */
class BtTestActivity : ComponentActivity() {

    private val permLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { begin() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = AccentColor, background = ScreenColor, surface = ScreenColor)) {
                Surface(Modifier.fillMaxSize(), color = ScreenColor) { Screen() }
            }
        }
        val needed = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 31) {
            needed += Manifest.permission.BLUETOOTH_CONNECT
            needed += Manifest.permission.BLUETOOTH_ADVERTISE
        }
        if (Build.VERSION.SDK_INT >= 33) needed += Manifest.permission.POST_NOTIFICATIONS
        val missing = needed.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) begin() else permLauncher.launch(missing.toTypedArray())
    }

    private fun begin() {
        try {
            startForegroundService(Intent(this, BtKeepService::class.java))
        } catch (e: Exception) {
            BtHid.add("Storitve ni bilo mogoče zagnati: ${e.message}")
            BtHid.start(this)
        }
    }

    private fun makeVisible() {
        try {
            startActivity(
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120)
            )
        } catch (e: Exception) {
            BtHid.add("Napaka: ${e.message}")
        }
    }

    private fun stopAll() {
        BtHid.stop()
        stopService(Intent(this, BtKeepService::class.java))
    }

    @Composable
    private fun Screen() {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Bluetooth preizkus", style = MaterialTheme.typography.titleLarge, color = TextColor)
            Text(BtHid.status, color = if (BtHid.status.startsWith("Povezano")) GreenColor else DimColor)

            Text("1. Seznanitev (samo prvič)", color = GlowColor)
            Button(onClick = { makeVisible() }, modifier = Modifier.fillMaxWidth()) {
                Text("Naj bo telefon viden (2 min)")
            }
            Text(
                "Daljinec ostane aktiven tudi, ko zapustiš to aplikacijo (vidiš obvestilo). Seznani telefon z boxom prek Bluetooth menija telefona.",
                color = LabelColor, fontSize = 14.sp
            )

            Text("2. Povezava", color = GlowColor)
            OutlinedButton(onClick = { BtHid.refreshBonded() }, modifier = Modifier.fillMaxWidth()) {
                Text("Osveži seznam seznanjenih naprav")
            }
            BtHid.bonded.forEach { d ->
                OutlinedButton(onClick = { BtHid.connectTo(d) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Poveži: ${BtHid.name(d)}")
                }
            }

            Text("3. Preizkus tipk (na TV-ju naj teče program)", color = GlowColor)
            Button(onClick = { BtHid.consumer(0x223, "Domov") }, modifier = Modifier.fillMaxWidth()) {
                Text("Domov (preizkus povezave)")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { BtHid.consumer(0x9C, "Program ▲") }, modifier = Modifier.weight(1f)) { Text("Program ▲") }
                Button(onClick = { BtHid.consumer(0x9D, "Program ▼") }, modifier = Modifier.weight(1f)) { Text("Program ▼") }
            }
            Button(onClick = { BtHid.keyboard(0x04, "Črka A") }, modifier = Modifier.fillMaxWidth()) {
                Text("Črka A (preizkus tipkanja v iskanju)")
            }

            OutlinedButton(onClick = { stopAll() }, modifier = Modifier.fillMaxWidth()) {
                Text("Ustavi Bluetooth daljinec")
            }

            Spacer(Modifier.height(8.dp))
            Text("Dnevnik", color = GlowColor)
            BtHid.log.forEach { Text(it, color = Color(0xFFB0B6BE), fontSize = 13.sp) }
        }
    }
}
