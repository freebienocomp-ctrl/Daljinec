package si.daljinec

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.net.Socket
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.concurrent.Executors
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509KeyManager
import javax.net.ssl.X509TrustManager
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder

// ---------- minimalni protobuf ----------

class PbWriter {
    private val out = ByteArrayOutputStream()

    private fun varint(value: Long) {
        var x = value
        while ((x and 0x7FL.inv()) != 0L) {
            out.write(((x and 0x7FL) or 0x80L).toInt())
            x = x ushr 7
        }
        out.write(x.toInt())
    }

    fun int(field: Int, v: Int): PbWriter {
        varint((field shl 3).toLong())
        varint(v.toLong())
        return this
    }

    fun bytes(field: Int, b: ByteArray): PbWriter {
        varint(((field shl 3) or 2).toLong())
        varint(b.size.toLong())
        out.write(b, 0, b.size)
        return this
    }

    fun str(field: Int, s: String): PbWriter = bytes(field, s.toByteArray(Charsets.UTF_8))
    fun msg(field: Int, m: PbWriter): PbWriter = bytes(field, m.toByteArray())
    fun toByteArray(): ByteArray = out.toByteArray()
}

class PbReader(private val data: ByteArray) {
    private val fields = HashMap<Int, MutableList<Any>>()
    private var pos = 0

    init {
        parse()
    }

    private fun readVarint(): Long {
        var result = 0L
        var shift = 0
        while (pos < data.size) {
            val b = data[pos++].toInt() and 0xFF
            result = result or ((b and 0x7F).toLong() shl shift)
            if ((b and 0x80) == 0) return result
            shift += 7
        }
        return result
    }

    private fun add(f: Int, v: Any) {
        fields.getOrPut(f) { mutableListOf() }.add(v)
    }

    private fun parse() {
        while (pos < data.size) {
            val tag = readVarint().toInt()
            val field = tag ushr 3
            when (tag and 7) {
                0 -> add(field, readVarint())
                2 -> {
                    val len = readVarint().toInt()
                    if (len < 0 || pos + len > data.size) return
                    add(field, data.copyOfRange(pos, pos + len))
                    pos += len
                }
                1 -> pos += 8
                5 -> pos += 4
                else -> return
            }
        }
    }

    fun has(f: Int): Boolean = fields.containsKey(f)
    fun long(f: Int): Long? = fields[f]?.firstOrNull() as? Long
    fun sub(f: Int): PbReader? = (fields[f]?.firstOrNull() as? ByteArray)?.let { PbReader(it) }
}

object Frames {
    fun write(out: OutputStream, data: ByteArray) {
        var n = data.size
        val hdr = ByteArrayOutputStream()
        while (true) {
            if ((n and 0x7F.inv()) == 0) {
                hdr.write(n)
                break
            }
            hdr.write((n and 0x7F) or 0x80)
            n = n ushr 7
        }
        out.write(hdr.toByteArray())
        out.write(data)
        out.flush()
    }

    fun read(inp: InputStream): ByteArray? {
        var len = 0
        var shift = 0
        while (true) {
            val b = inp.read()
            if (b < 0) return null
            len = len or ((b and 0x7F) shl shift)
            if ((b and 0x80) == 0) break
            shift += 7
        }
        val buf = ByteArray(len)
        var off = 0
        while (off < len) {
            val r = inp.read(buf, off, len - off)
            if (r < 0) return null
            off += r
        }
        return buf
    }
}

// ---------- certifikat in TLS ----------

private class FixedKeyManager(
    private val key: PrivateKey,
    private val chain: Array<X509Certificate>
) : X509KeyManager {
    override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf("client")
    override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = "client"
    override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String>? = null
    override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String? = null
    override fun getCertificateChain(alias: String?): Array<X509Certificate> = chain
    override fun getPrivateKey(alias: String?): PrivateKey = key
}

object Identity {
    private val PASS = "daljinec".toCharArray()

    fun load(ctx: Context): KeyStore {
        val file = File(ctx.filesDir, "client.p12")
        val ks = KeyStore.getInstance("PKCS12")
        if (file.exists()) {
            file.inputStream().use { ks.load(it, PASS) }
            return ks
        }
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        val name = X500Name("CN=daljinec")
        val now = System.currentTimeMillis()
        val builder = JcaX509v3CertificateBuilder(
            name,
            BigInteger.valueOf(now),
            Date(now - 86400000L),
            Date(now + 20L * 365L * 86400000L),
            name,
            kp.public
        )
        val signer = JcaContentSignerBuilder("SHA256withRSA").build(kp.private)
        val cert = JcaX509CertificateConverter().getCertificate(builder.build(signer))
        ks.load(null, null)
        ks.setKeyEntry("client", kp.private, PASS, arrayOf(cert))
        file.outputStream().use { ks.store(it, PASS) }
        return ks
    }

    fun sslContext(ks: KeyStore): SSLContext {
        val key = ks.getKey("client", PASS) as PrivateKey
        val chain = arrayOf(ks.getCertificate("client") as X509Certificate)
        val trustAll = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(arrayOf<KeyManager>(FixedKeyManager(key, chain)), arrayOf<TrustManager>(trustAll), null)
        return ctx
    }
}

// ---------- uparjanje (vrata 6467) ----------

class Pairing(ctx: Context, private val host: String) {
    private val ks = Identity.load(ctx)
    private val ssl = Identity.sslContext(ks)
    private var socket: SSLSocket? = null
    private lateinit var inp: InputStream
    private lateinit var out: OutputStream

    private fun frame(build: PbWriter.() -> Unit): ByteArray {
        val w = PbWriter()
        w.int(1, 2)
        w.int(2, 200)
        w.build()
        return w.toByteArray()
    }

    private fun exchange(data: ByteArray): PbReader {
        Frames.write(out, data)
        val resp = Frames.read(inp) ?: throw IllegalStateException("Povezava prekinjena")
        val r = PbReader(resp)
        val st = r.long(2) ?: 200L
        if (st != 200L) throw IllegalStateException("Box je zavrnil uparjanje (status $st)")
        return r
    }

    fun begin() {
        val s = ssl.socketFactory.createSocket(host, 6467) as SSLSocket
        s.startHandshake()
        socket = s
        inp = s.inputStream
        out = s.outputStream
        exchange(frame { msg(10, PbWriter().str(1, "atvremote").str(2, "Daljinec")) })
        exchange(frame { msg(20, PbWriter().msg(1, PbWriter().int(1, 3).int(2, 6)).int(3, 1)) })
        exchange(frame { msg(30, PbWriter().msg(1, PbWriter().int(1, 3).int(2, 6)).int(2, 1)) })
    }

    private fun unsigned(b: BigInteger): ByteArray {
        val a = b.toByteArray()
        return if (a.size > 1 && a[0] == 0.toByte()) a.copyOfRange(1, a.size) else a
    }

    private fun hex(s: String): ByteArray =
        ByteArray(s.length / 2) { s.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

    fun finish(code: String) {
        val c = code.trim().uppercase()
        require(c.length == 6 && c.all { it in "0123456789ABCDEF" }) { "Koda ima 6 znakov (0-9, A-F)" }
        val clientKey = ks.getCertificate("client").publicKey as RSAPublicKey
        val serverKey = socket!!.session.peerCertificates[0].publicKey as RSAPublicKey
        val md = MessageDigest.getInstance("SHA-256")
        md.update(unsigned(clientKey.modulus))
        md.update(unsigned(clientKey.publicExponent))
        md.update(unsigned(serverKey.modulus))
        md.update(unsigned(serverKey.publicExponent))
        md.update(hex(c.substring(2)))
        val hash = md.digest()
        require(hash[0] == c.substring(0, 2).toInt(16).toByte()) { "Napačna koda" }
        exchange(frame { msg(40, PbWriter().bytes(1, hash)) })
        close()
    }

    fun close() {
        try { socket?.close() } catch (_: Exception) {}
    }
}

// ---------- daljinec (vrata 6466) ----------

class RemoteConnection(
    private val ctx: Context,
    private val host: String,
    private val onState: (Boolean) -> Unit
) {
    @Volatile private var socket: SSLSocket? = null
    private val lock = Any()
    private var out: OutputStream? = null

    fun run() {
        val ks = Identity.load(ctx)
        val s = Identity.sslContext(ks).socketFactory.createSocket(host, 6466) as SSLSocket
        s.startHandshake()
        socket = s
        val inp = s.inputStream
        synchronized(lock) { out = s.outputStream }
        try {
            while (true) {
                val data = Frames.read(inp) ?: break
                val r = PbReader(data)
                when {
                    r.has(1) -> send(
                        PbWriter().msg(
                            1,
                            PbWriter().int(1, 622).msg(
                                2,
                                PbWriter()
                                    .str(1, "Daljinec").str(2, "Daljinec")
                                    .int(3, 1).str(4, "1")
                                    .str(5, "androidtv-remote").str(6, "1.0.0")
                            )
                        )
                    )
                    r.has(2) -> send(PbWriter().msg(2, PbWriter().int(1, 622)))
                    r.has(8) -> {
                        val v = r.sub(8)?.long(1) ?: 0L
                        send(PbWriter().msg(9, PbWriter().int(1, v.toInt())))
                    }
                    r.has(40) -> onState(true)
                }
            }
        } finally {
            onState(false)
            synchronized(lock) { out = null }
            try { s.close() } catch (_: Exception) {}
        }
    }

    private fun send(w: PbWriter) {
        synchronized(lock) {
            val o = out ?: return
            try { Frames.write(o, w.toByteArray()) } catch (_: Exception) {}
        }
    }

    fun key(code: Int) = send(PbWriter().msg(10, PbWriter().int(1, code).int(2, 3)))

    fun close() {
        try { socket?.close() } catch (_: Exception) {}
    }
}

// ---------- iskanje boxov v omrežju ----------

class Discovery(ctx: Context, private val onFound: (String, String) -> Unit) {
    private val nsd = ctx.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val queue = ArrayDeque<NsdServiceInfo>()
    private var resolving = false

    private val listener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(serviceType: String) {}
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            synchronized(queue) { queue.add(serviceInfo) }
            next()
        }
        override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
        override fun onDiscoveryStopped(serviceType: String) {}
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
    }

    private fun next() {
        var picked: NsdServiceInfo? = null
        synchronized(queue) {
            if (!resolving && queue.isNotEmpty()) {
                resolving = true
                picked = queue.removeFirst()
            }
        }
        val info = picked ?: return
        nsd.resolveService(info, object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) { done() }
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                onFound(serviceInfo.serviceName, serviceInfo.host?.hostAddress ?: "")
                done()
            }
        })
    }

    private fun done() {
        synchronized(queue) { resolving = false }
        next()
    }

    fun start() {
        try {
            nsd.discoverServices("_androidtvremote2._tcp.", NsdManager.PROTOCOL_DNS_SD, listener)
        } catch (_: Exception) {}
    }

    fun stop() {
        try { nsd.stopServiceDiscovery(listener) } catch (_: Exception) {}
    }
}

// ---------- tipke ----------

fun charToKey(c: Char): Int? = when (val ch = c.lowercaseChar()) {
    in 'a'..'z' -> 29 + (ch - 'a')
    in '0'..'9' -> 7 + (ch - '0')
    ' ' -> 62
    'č', 'ć' -> 31
    'š' -> 47
    'ž' -> 54
    'đ' -> 32
    else -> null
}

// ---------- stanje aplikacije ----------

class AppState(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("daljinec", Context.MODE_PRIVATE)
    private val main = Handler(Looper.getMainLooper())
    private val net = Executors.newSingleThreadExecutor()
    private val keys = Executors.newSingleThreadExecutor()

    var host by mutableStateOf(prefs.getString("host", "") ?: "")
    var paired by mutableStateOf(prefs.getBoolean("paired", false))
    var status by mutableStateOf("")
    var connected by mutableStateOf(false)
    var waitingCode by mutableStateOf(false)
    var busy by mutableStateOf(false)
    var channel by mutableStateOf(prefs.getInt("channel", 1))
    val found = mutableStateListOf<Pair<String, String>>()

    private var pairing: Pairing? = null
    private var remote: RemoteConnection? = null
    private var gen = 0
    private var running = false
    private var entry = ""
    private var lastDigit = 0L

    fun addFound(name: String, ip: String) {
        if (ip.isEmpty() || ip.contains(':')) return
        main.post { if (found.none { it.second == ip }) found.add(name to ip) }
    }

    fun startPairing() {
        busy = true
        status = "Povezujem…"
        net.execute {
            try {
                pairing?.close()
                val p = Pairing(ctx, host)
                p.begin()
                pairing = p
                waitingCode = true
                status = "Vpiši kodo, ki je prikazana na TV-ju."
            } catch (e: Exception) {
                status = "Napaka: ${e.message}"
            }
            busy = false
        }
    }

    fun finishPairing(code: String) {
        busy = true
        net.execute {
            try {
                pairing!!.finish(code)
                prefs.edit().putString("host", host).putBoolean("paired", true).apply()
                waitingCode = false
                paired = true
                status = ""
                connect()
            } catch (e: Exception) {
                status = "Napaka: ${e.message}"
            }
            busy = false
        }
    }

    fun resetPairing() {
        disconnect()
        prefs.edit().putBoolean("paired", false).apply()
        paired = false
        waitingCode = false
        status = ""
    }

    fun connect() {
        if (running || host.isEmpty()) return
        running = true
        val g = ++gen
        Thread {
            while (running && g == gen) {
                val rc = RemoteConnection(ctx, host) { ok ->
                    connected = ok
                    if (ok) status = ""
                }
                remote = rc
                try {
                    rc.run()
                } catch (e: Exception) {
                    status = "Ni povezave: ${e.message}"
                }
                if (running && g == gen) Thread.sleep(2000)
            }
        }.start()
    }

    fun disconnect() {
        running = false
        gen++
        remote?.close()
        remote = null
        connected = false
    }

    fun key(code: Int) {
        keys.execute { remote?.key(code) }
    }

    private fun saveChannel(c: Int) {
        channel = c
        prefs.edit().putInt("channel", c).apply()
    }

    /** Številka s številčnice: pošlje tipko in si zapomni program. */
    fun digit(n: Int) {
        val now = System.currentTimeMillis()
        entry = if (now - lastDigit < 2000 && entry.length < 4) entry + n else n.toString()
        lastDigit = now
        entry.toIntOrNull()?.let { if (it > 0) saveChannel(it) }
        key(7 + n)
    }

    /** Prog + / Prog −: vtipka naslednjo ali prejšnjo številko programa. */
    fun channelStep(d: Int) {
        val c = (channel + d).coerceAtLeast(1)
        saveChannel(c)
        entry = ""
        keys.execute {
            for (ch in c.toString()) {
                remote?.key(7 + (ch - '0'))
                Thread.sleep(150)
            }
        }
    }

    fun sync(old: String, new: String) {
        var common = 0
        while (common < old.length && common < new.length && old[common] == new[common]) common++
        val del = old.length - common
        val add = new.substring(common)
        keys.execute {
            repeat(del) {
                remote?.key(67)
                Thread.sleep(60)
            }
            for (ch in add) {
                val k = charToKey(ch)
                if (k != null) {
                    remote?.key(k)
                    Thread.sleep(60)
                }
            }
        }
    }
}

// ---------- zasloni ----------

class MainActivity : ComponentActivity() {
    private lateinit var state: AppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = AppState(applicationContext)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) { App(state) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (state.paired) state.connect()
    }

    override fun onStop() {
        super.onStop()
        state.disconnect()
    }
}

@Composable
fun App(s: AppState) {
    if (s.paired) RemoteScreen(s) else SetupScreen(s)
}

@Composable
fun SetupScreen(s: AppState) {
    val ctx = LocalContext.current
    var code by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val d = Discovery(ctx) { name, ip -> s.addFound(name, ip) }
        d.start()
        onDispose { d.stop() }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Povezava z EON boxom", style = MaterialTheme.typography.titleLarge)
        Text("Telefon in box morata biti v istem Wi-Fi omrežju, box pa vklopljen.")
        if (s.found.isNotEmpty()) {
            Text("Najdeni boxi:")
            s.found.forEach { (name, ip) ->
                OutlinedButton(onClick = { s.host = ip }, modifier = Modifier.fillMaxWidth()) {
                    Text("$name ($ip)")
                }
            }
        }
        OutlinedTextField(
            value = s.host,
            onValueChange = { s.host = it.trim() },
            label = { Text("IP naslov boxa") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { s.startPairing() },
            enabled = s.host.isNotEmpty() && !s.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Upari") }

        if (s.waitingCode) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase().take(6) },
                label = { Text("Koda s TV-ja (6 znakov)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { s.finishPairing(code) },
                enabled = code.length == 6 && !s.busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Potrdi kodo") }
        }
        if (s.status.isNotEmpty()) Text(s.status)
    }
}

@Composable
fun RBtn(
    label: String,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    if (primary) {
        Button(onClick = onClick, modifier = modifier, shape = shape, contentPadding = PaddingValues(0.dp)) {
            Text(label, fontSize = 20.sp)
        }
    } else {
        FilledTonalButton(onClick = onClick, modifier = modifier, shape = shape, contentPadding = PaddingValues(0.dp)) {
            Text(label, fontSize = 20.sp)
        }
    }
}

@Composable
fun RemoteScreen(s: AppState) {
    var text by remember { mutableStateOf("") }
    var testCode by remember { mutableStateOf("") }
    val gap = Arrangement.spacedBy(6.dp)

    Column(
        Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (s.connected) "● Povezano  ·  Program ${s.channel}" else "○ " + s.status.ifEmpty { "Povezujem…" },
                Modifier.weight(1f),
                fontSize = 13.sp
            )
            TextButton(onClick = { s.resetPairing() }) { Text("Nastavitve") }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { n ->
                    s.sync(text, n)
                    text = n
                },
                placeholder = { Text("Išči (tipkaj sem)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(onClick = { s.key(66) }) { Text("Enter") }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = testCode,
                onValueChange = { testCode = it.filter { c -> c.isDigit() }.take(3) },
                placeholder = { Text("Test koda") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(onClick = { testCode.toIntOrNull()?.let { s.key(it) } }) { Text("Pošlji") }
        }

        Column(Modifier.weight(4f).fillMaxWidth(), verticalArrangement = gap) {
            listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { r ->
                Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
                    r.forEach { n ->
                        RBtn(n.toString(), Modifier.weight(1f).fillMaxHeight()) { s.digit(n) }
                    }
                }
            }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
                Spacer(Modifier.weight(1f))
                RBtn("0", Modifier.weight(1f).fillMaxHeight()) { s.digit(0) }
                Spacer(Modifier.weight(1f))
            }
        }

        Column(Modifier.weight(3.4f).fillMaxWidth(), verticalArrangement = gap) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
                Spacer(Modifier.weight(1f))
                RBtn("▲", Modifier.weight(1f).fillMaxHeight()) { s.key(19) }
                Spacer(Modifier.weight(1f))
            }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
                RBtn("◀", Modifier.weight(1f).fillMaxHeight()) { s.key(21) }
                RBtn("OK", Modifier.weight(1f).fillMaxHeight(), primary = true) { s.key(23) }
                RBtn("▶", Modifier.weight(1f).fillMaxHeight()) { s.key(22) }
            }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
                Spacer(Modifier.weight(1f))
                RBtn("▼", Modifier.weight(1f).fillMaxHeight()) { s.key(20) }
                Spacer(Modifier.weight(1f))
            }
        }

        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) {
            RBtn("← Nazaj", Modifier.weight(1f).fillMaxHeight()) { s.key(4) }
            RBtn("Domov", Modifier.weight(1f).fillMaxHeight()) { s.key(3) }
        }

        Row(Modifier.weight(2.2f).fillMaxWidth(), horizontalArrangement = gap) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = gap) {
                RBtn("Vol +", Modifier.weight(1f).fillMaxWidth()) { s.key(24) }
                RBtn("Vol −", Modifier.weight(1f).fillMaxWidth()) { s.key(25) }
            }
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                RBtn("Utišaj", Modifier.fillMaxWidth().height(56.dp)) { s.key(164) }
            }
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = gap) {
                RBtn("Prog ▲", Modifier.weight(1f).fillMaxWidth()) { s.channelStep(1) }
                RBtn("Prog ▼", Modifier.weight(1f).fillMaxWidth()) { s.channelStep(-1) }
            }
        }
    }
}
