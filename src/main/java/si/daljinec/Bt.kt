package si.daljinec

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

/** Preizkus: telefon kot Bluetooth daljinec za box. */
@SuppressLint("MissingPermission")
class BtTestActivity : ComponentActivity() {
    private var hid: BluetoothHidDevice? = null
    private var host: BluetoothDevice? = null
    private val log = mutableStateListOf<String>()
    private var status by mutableStateOf("Ni povezano")
    private val bonded = mutableStateListOf<BluetoothDevice>()
    private val cbExec = Executors.newSingleThreadExecutor()
    private val sendExec = Executors.newSingleThreadExecutor()

    private val adapter: BluetoothAdapter?
        get() = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val timeFmt = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US)
    private var wantedHost: BluetoothDevice? = null
    private var retries = 0

    private fun add(s: String) {
        val line = timeFmt.format(java.util.Date()) + "  " + s
        runOnUiThread {
            log.add(0, line)
            if (log.size > 40) log.removeAt(log.size - 1)
        }
    }

    private fun name(d: BluetoothDevice): String = try { d.name ?: d.address } catch (_: Exception) { d.address }

    private val permLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { start() }

    private val callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            add(if (registered) "Registriran kot Bluetooth daljinec" else "Registracija preklicana")
            if (registered && pluggedDevice != null) hid?.connect(pluggedDevice)
        }

        override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    host = device
                    retries = 0
                    runOnUiThread { status = "Povezano: ${name(device)}" }
                    add("Povezano: ${name(device)}")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (host == device) host = null
                    runOnUiThread { status = "Ni povezano" }
                    add("Prekinjeno: ${name(device)}")
                    // samodejno: še dva poskusa ponovne povezave
                    if (device == wantedHost && retries < 2) {
                        retries++
                        Thread.sleep(1500)
                        add("Ponovni poskus $retries…")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = AccentColor, background = ScreenColor, surface = ScreenColor)) {
                Surface(Modifier.fillMaxSize(), color = ScreenColor) { Screen() }
            }
        }
        val needed = if (Build.VERSION.SDK_INT >= 31)
            listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE)
        else listOf()
        val missing = needed.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) start() else permLauncher.launch(missing.toTypedArray())
    }

    private fun register() {
        val h = hid ?: run { add("Bluetooth storitev še ni pripravljena"); return }
        try { h.unregisterApp() } catch (_: Exception) {}
        val sdp = BluetoothHidDeviceAppSdpSettings(
            "Daljinec", "Daljinec remote", "Daljinec",
            BluetoothHidDevice.SUBCLASS1_COMBO, HID_DESCRIPTOR
        )
        val reg = h.registerApp(sdp, null, null, cbExec, callback)
        add(if (reg) "Registracija poslana…" else "Registracija ni uspela")
    }

    private fun start() {
        val a = adapter
        if (a == null) { add("Telefon nima Bluetootha"); return }
        if (!a.isEnabled) { add("Vklopi Bluetooth na telefonu in odpri preizkus znova"); return }
        try {
            refreshBonded()
            val ok = a.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    hid = proxy as BluetoothHidDevice
                    register()
                }

                override fun onServiceDisconnected(profile: Int) {
                    hid = null
                    add("Bluetooth storitev prekinjena")
                }
            }, BluetoothProfile.HID_DEVICE)
            if (!ok) add("Telefon ne podpira Bluetooth daljinca (HID)")
        } catch (e: Exception) {
            add("Napaka: ${e.message}")
        }
    }

    private fun refreshBonded() {
        bonded.clear()
        try { adapter?.bondedDevices?.let { bonded.addAll(it) } } catch (_: Exception) {}
    }

    private fun makeVisible() {
        try {
            startActivity(
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120)
            )
        } catch (e: Exception) {
            add("Napaka: ${e.message}")
        }
    }

    private fun connectTo(d: BluetoothDevice) {
        val h = hid
        if (h == null) { add("Bluetooth daljinec še ni pripravljen"); return }
        wantedHost = d
        retries = 0
        add(if (h.connect(d)) "Povezujem z ${name(d)}…" else "Povezava z ${name(d)} ni uspela")
    }

    /** Tipka daljinca (consumer usage), npr. 0x9C = program gor. */
    private fun consumer(usage: Int, label: String) {
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
    private fun keyboard(usage: Int, label: String) {
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

    override fun onDestroy() {
        try {
            hid?.unregisterApp()
            adapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hid)
        } catch (_: Exception) {}
        super.onDestroy()
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
            Text(status, color = if (status.startsWith("Povezano")) GreenColor else DimColor)

            Text("1. Seznanitev (samo prvič)", color = GlowColor)
            Button(onClick = { makeVisible() }, modifier = Modifier.fillMaxWidth()) {
                Text("Naj bo telefon viden (2 min)")
            }
            Text(
                "Na boxu odpri Nastavitve → Daljinci in dodatki → Dodaj dodatek in izberi ta telefon. Potrdi kodo, če jo box pokaže.",
                color = LabelColor, fontSize = 14.sp
            )

            Text("2. Povezava (če se ne poveže sam)", color = GlowColor)
            OutlinedButton(onClick = { register() }, modifier = Modifier.fillMaxWidth()) {
                Text("Ponovno registriraj daljinec")
            }
            OutlinedButton(onClick = { refreshBonded() }, modifier = Modifier.fillMaxWidth()) {
                Text("Osveži seznam seznanjenih naprav")
            }
            bonded.forEach { d ->
                OutlinedButton(onClick = { connectTo(d) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Poveži: ${name(d)}")
                }
            }

            Text("3. Preizkus tipk (na TV-ju naj teče program)", color = GlowColor)
            Button(onClick = { consumer(0x223, "Domov") }, modifier = Modifier.fillMaxWidth()) {
                Text("Domov (preizkus povezave)")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { consumer(0x9C, "Program ▲") }, modifier = Modifier.weight(1f)) { Text("Program ▲") }
                Button(onClick = { consumer(0x9D, "Program ▼") }, modifier = Modifier.weight(1f)) { Text("Program ▼") }
            }
            Button(onClick = { keyboard(0x04, "Črka A") }, modifier = Modifier.fillMaxWidth()) {
                Text("Črka A (preizkus tipkanja v iskanju)")
            }

            Spacer(Modifier.height(8.dp))
            Text("Dnevnik", color = GlowColor)
            log.forEach { Text(it, color = Color(0xFFB0B6BE), fontSize = 13.sp) }
        }
    }
}
