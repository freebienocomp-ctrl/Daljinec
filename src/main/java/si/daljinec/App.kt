package si.daljinec

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder

// ---------- barve ----------

val ScreenColor = Color(0xFF050607)
val TextColor = Color(0xFFEEF0F2)
val DimColor = Color(0xFF8A9099)
val LabelColor = Color(0xFF9AA0A8)
val AccentColor = Color(0xFF3B82F6)
val GreenColor = Color(0xFF4ADE80)
val GlowColor = Color(0xFF60A5FA)
val ArrowColor = Color(0xFFC8CCD2)
val ArrowLit = Color(0xFF93C5FD)
val BodyBrush = Brush.verticalGradient(listOf(Color(0xFF0C0E10), Color(0xFF060708)))
val KeyBrush = Brush.verticalGradient(listOf(Color(0xFF2C3138), Color(0xFF22262C)))

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
        val holder = builder.build(signer)
        val cert = JcaX509CertificateConverter().getCertificate(holder)
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

    /** dir: 1 = tipka pritisnjena, 2 = tipka spuščena, 3 = kratek pritisk */
    fun key(code: Int, dir: Int = 3) = send(PbWriter().msg(10, PbWriter().int(1, code).int(2, dir)))

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

// ---------- seznam programov ----------

/** "1-10, 12, 15-20" -> seznam številk */
fun parseChannels(t: String): List<Int> {
    val out = sortedSetOf<Int>()
    for (part in t.split(',', ';')) {
        val p = part.trim()
        if (p.isEmpty()) continue
        val ab = p.split('-')
        if (ab.size == 2) {
            val a = ab[0].trim().toIntOrNull()
            val b = ab[1].trim().toIntOrNull()
            if (a != null && b != null && a in 1..b && b - a < 2000) {
                for (i in a..b) out.add(i)
            }
        } else {
            p.toIntOrNull()?.let { if (it > 0) out.add(it) }
        }
    }
    return out.toList()
}

// ---------- EON tipkovnica ----------

/** Vrstice 1-5 EON tipkovnice, vsaka ima 6 stolpcev. */
val EON_ROWS = listOf(
    "ABCČDE",
    "FGHIJK",
    "LMNOPR",
    "SŠTUVZ",
    "Ž:;.,-"
)

/** Vrne (vrstica, stolpec) za znak, (-1, -1) za presledek ali null, če znaka ni. */
fun eonPos(ch: Char): Pair<Int, Int>? {
    val c = when (val u = ch.uppercaseChar()) {
        'Ć' -> 'Č'
        'Đ' -> 'D'
        else -> u
    }
    if (c == ' ') return Pair(-1, -1)
    EON_ROWS.forEachIndexed { r, row ->
        val col = row.indexOf(c)
        if (col >= 0) return Pair(r + 1, col)
    }
    return null
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
    var channelList by mutableStateOf(prefs.getString("chlist", "") ?: "")
    var stepDelay by mutableStateOf(prefs.getInt("step", 80))
    var homeWait by mutableStateOf(prefs.getInt("home", 500))
    var screenWait by mutableStateOf(prefs.getInt("screen", 1000))
    var showSettings by mutableStateOf(false)
    var searchText by mutableStateOf("")
    var searchOpen by mutableStateOf(false)
    var searchReady by mutableStateOf(false)
    var ledTick by mutableStateOf(0)
    val found = mutableStateListOf<Pair<String, String>>()

    private var pairing: Pairing? = null
    private var remote: RemoteConnection? = null
    private var gen = 0
    private var running = false
    private var entry = ""
    private var lastDigit = 0L

    // položaj na EON tipkovnici (uporablja samo nit "keys")
    // kr = 0 pomeni: izbrana je tipka za brisanje
    private var kbOpen = false
    private var kr = 1
    private var kc = 0

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

    // ---------- tipke ----------

    /** Kratek pritisk (konča iskanje). */
    fun press(code: Int) {
        closeSearch()
        ledTick++
        keys.execute { remote?.key(code, 3) }
    }

    /** Pravo držanje: tipka pritisnjena. */
    fun keyDown(code: Int) {
        closeSearch()
        ledTick++
        keys.execute { remote?.key(code, 1) }
    }

    /** Pravo držanje: tipka spuščena. */
    fun keyUp(code: Int) {
        keys.execute { remote?.key(code, 2) }
    }

    /** Preizkus: pošlje "pritisnjeno", počaka 150 ms in pošlje "spuščeno" (kot fizični daljinec). */
    fun holdPress(code: Int) {
        ledTick++
        keys.execute {
            remote?.key(code, 1)
            Thread.sleep(150)
            remote?.key(code, 2)
        }
    }

    private fun saveChannel(c: Int) {
        channel = c
        prefs.edit().putInt("channel", c).apply()
    }

    fun saveChannelList(t: String) {
        channelList = t
        prefs.edit().putString("chlist", t).apply()
    }

    fun saveTimings(step: Int, home: Int, screen: Int) {
        stepDelay = step
        homeWait = home
        screenWait = screen
        prefs.edit().putInt("step", step).putInt("home", home).putInt("screen", screen).apply()
    }

    /** Številka s številčnice: pošlje tipko in si zapomni program. */
    fun digit(n: Int) {
        val now = System.currentTimeMillis()
        entry = if (now - lastDigit < 2000 && entry.length < 4) entry + n else n.toString()
        lastDigit = now
        entry.toIntOrNull()?.let { if (it > 0) saveChannel(it) }
        press(7 + n)
    }

    /** Prog + / Prog −: takoj vtipka naslednji obstoječi program in potrdi z OK. */
    fun channelStep(d: Int) {
        val list = parseChannels(channelList)
        val c = when {
            list.isEmpty() -> (channel + d).coerceAtLeast(1)
            d > 0 -> list.firstOrNull { it > channel } ?: list.first()
            else -> list.lastOrNull { it < channel } ?: list.last()
        }
        saveChannel(c)
        entry = ""
        closeSearch()
        ledTick++
        keys.execute {
            for (ch in c.toString()) {
                remote?.key(7 + (ch - '0'))
                Thread.sleep(100)
            }
            remote?.key(23)
            Thread.sleep(100)
        }
    }

    // ---------- iskanje na EON tipkovnici ----------

    /** Tap na lupo: razširi polje in takoj začne odpirati iskanje na TV-ju. */
    fun openSearchUi() {
        if (searchOpen) return
        searchOpen = true
        searchReady = false
        searchText = ""
        ledTick++
        keys.execute {
            openSearchIfNeeded()
            searchReady = true
        }
    }

    /** ✕ ali katerakoli druga tipka: zapre polje. */
    fun closeSearch() {
        if (searchOpen || searchText.isNotEmpty()) {
            searchOpen = false
            searchReady = false
            searchText = ""
        }
        keys.execute { kbOpen = false }
    }

    /** Klic ob vsaki spremembi besedila v iskalnem polju. */
    fun onSearchChange(n: String) {
        val old = searchText
        searchText = n
        var common = 0
        while (common < old.length && common < n.length && old[common] == n[common]) common++
        val delCount = old.substring(common).count { eonPos(it) != null }
        val add = n.substring(common).mapNotNull { eonPos(it) }
        if (delCount == 0 && add.isEmpty()) return
        keys.execute { typeOnTv(delCount, add) }
    }

    private fun step(code: Int, wait: Long = stepDelay.toLong()) {
        remote?.key(code)
        Thread.sleep(wait)
    }

    private fun moveTo(tr: Int, tc: Int) {
        while (kr < tr) { step(20); kr++ }
        while (kr > tr) { step(19); kr-- }
        while (kc < tc) { step(22); kc++ }
        while (kc > tc) { step(21); kc-- }
    }

    private fun openSearchIfNeeded() {
        if (kbOpen) return
        step(3, homeWait.toLong())     // Domov, počakaj, da se naloži
        step(21)                       // levo -> lupa
        step(21)                       // levo (zavarovanje)
        step(21, screenWait.toLong())  // levo (zavarovanje), odpre iskanje
        step(20)                       // dol -> mikrofon
        step(20)                       // dol -> A
        kr = 1
        kc = 0
        kbOpen = true
    }

    private fun typeOnTv(del: Int, add: List<Pair<Int, Int>>) {
        val press = stepDelay * 2L
        if (!kbOpen) {
            if (add.isEmpty()) return
            openSearchIfNeeded()
        }
        if (del > 0) {
            // briši: nad E je tipka za brisanje, na njej ostanemo
            if (kr != 0) {
                moveTo(1, 5)
                step(19, 250)
                kr = 0
            }
            repeat(del) { step(23, press) }
        }
        if (add.isNotEmpty() && kr == 0) {
            // z brisanja nazaj na tipkovnico, na A
            step(20, 250)
            repeat(5) { step(21) }
            kr = 1
            kc = 0
        }
        for ((tr, tc) in add) {
            if (tr == -1) {
                // presledek: z vrstice Ž, stolpec 2, en korak dol
                moveTo(5, 2)
                step(20)
                step(23, press)
                step(19)
                repeat(5) { step(21) }
                kr = 5
                kc = 0
                continue
            }
            moveTo(tr, tc)
            step(23, press)
        }
    }
}

// ---------- glavna aktivnost ----------

class MainActivity : ComponentActivity() {
    private lateinit var state: AppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        state = AppState(applicationContext)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = AccentColor,
                    onPrimary = Color.White,
                    background = ScreenColor,
                    surface = ScreenColor,
                    onBackground = TextColor,
                    onSurface = TextColor
                )
            ) {
                Surface(Modifier.fillMaxSize(), color = ScreenColor) { App(state) }
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
    when {
        !s.paired -> SetupScreen(s)
        s.showSettings -> SettingsScreen(s)
        else -> RemoteScreen(s)
    }
}

// ---------- uparjanje ----------

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

// ---------- nastavitve ----------

@Composable
fun SettingsScreen(s: AppState) {
    var list by remember { mutableStateOf(s.channelList) }
    var stepText by remember { mutableStateOf(s.stepDelay.toString()) }
    var homeText by remember { mutableStateOf(s.homeWait.toString()) }
    var screenText by remember { mutableStateOf(s.screenWait.toString()) }
    var testCode by remember { mutableStateOf("") }
    val count = parseChannels(list).size

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nastavitve", style = MaterialTheme.typography.titleLarge)
        Text("Vpiši programe, ki obstajajo, npr. 1-10, 12-45, 50. Prog ▲/▼ bosta preskočila manjkajoče. Prazno = vsi po vrsti.")
        OutlinedTextField(
            value = list,
            onValueChange = { list = it },
            label = { Text("Seznam programov") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(if (count == 0) "Seznam je prazen (vsi programi po vrsti)." else "Prepoznanih programov: $count")
        OutlinedTextField(
            value = stepText,
            onValueChange = { stepText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text("Premor med koraki pri iskanju (ms)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Manjše število = hitrejše tipkanje (5–1000). Če črke manjkajo, ga povečaj.", color = DimColor, fontSize = 13.sp)
        OutlinedTextField(
            value = homeText,
            onValueChange = { homeText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text("Čakanje po Domov (ms)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Koliko časa aplikacija počaka po Domov, preden pošlje ◀ (50–5000). Če iskanje ne odpre, ga povečaj.", color = DimColor, fontSize = 13.sp)
        OutlinedTextField(
            value = screenText,
            onValueChange = { screenText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text("Čakanje na odprtje iskanja (ms)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Koliko časa aplikacija počaka po ◀, da EON odpre iskanje (100–5000). Če se prva črka ne vpiše, ga povečaj.", color = DimColor, fontSize = 13.sp)
        Button(
            onClick = {
                s.saveChannelList(list)
                s.saveTimings(
                    (stepText.toIntOrNull() ?: 80).coerceIn(5, 1000),
                    (homeText.toIntOrNull() ?: 500).coerceIn(50, 5000),
                    (screenText.toIntOrNull() ?: 1000).coerceIn(100, 5000)
                )
                s.showSettings = false
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Shrani") }
        OutlinedButton(
            onClick = { s.showSettings = false },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Nazaj") }

        Spacer(Modifier.height(8.dp))
        Text("Preizkus kode tipke", style = MaterialTheme.typography.titleMedium)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = testCode,
                onValueChange = { testCode = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("Test koda") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { testCode.toIntOrNull()?.let { s.press(it) } }) { Text("Pošlji") }
        }
        OutlinedButton(
            onClick = { testCode.toIntOrNull()?.let { s.holdPress(it) } },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Pošlji (drži)") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                s.showSettings = false
                s.resetPairing()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ponovno upari box") }
    }
}

// ---------- gradniki daljinca ----------

/** Okrogla tipka: ob pritisku se nagne proti prstu, zasveti in se prožno vrne. */
@Composable
fun RoundKey(
    diameter: Dp,
    onDown: () -> Unit,
    onUp: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val rx = remember { Animatable(0f) }
    val ry = remember { Animatable(0f) }
    val sc = remember { Animatable(1f) }
    val glow = remember { Animatable(0f) }
    val down by rememberUpdatedState(onDown)
    val up by rememberUpdatedState(onUp)

    Box(
        Modifier
            .size(diameter)
            .graphicsLayer {
                rotationX = rx.value
                rotationY = ry.value
                scaleX = sc.value
                scaleY = sc.value
                cameraDistance = 10f * density
            }
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(KeyBrush)
            .border(1.5.dp, GlowColor.copy(alpha = 0.8f * glow.value), CircleShape)
            .pointerInput(Unit) {
                val hw = size.width / 2f
                val hh = size.height / 2f
                detectTapGestures(onPress = { o ->
                    val nx = ((o.x - hw) / hw).coerceIn(-1f, 1f)
                    val ny = ((o.y - hh) / hh).coerceIn(-1f, 1f)
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    down()
                    scope.launch { rx.animateTo(-ny * 16f, tween(70)) }
                    scope.launch { ry.animateTo(nx * 16f, tween(70)) }
                    scope.launch { sc.animateTo(0.94f, tween(70)) }
                    scope.launch { glow.snapTo(1f) }
                    tryAwaitRelease()
                    up()
                    scope.launch { rx.animateTo(0f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                    scope.launch { ry.animateTo(0f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                    scope.launch { sc.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                    scope.launch { glow.animateTo(0f, tween(450)) }
                })
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun IconLabel(icon: ImageVector, label: String, k: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp * k), tint = TextColor)
        Text(label, fontSize = (11 * k).sp, color = LabelColor)
    }
}

@Composable
fun Arrow(icon: ImageVector, desc: String, lit: Boolean, modifier: Modifier, k: Float) {
    val c by animateColorAsState(
        if (lit) ArrowLit else ArrowColor,
        tween(if (lit) 60 else 350),
        label = "arrow"
    )
    Icon(icon, contentDescription = desc, modifier = modifier.size(42.dp * k), tint = c)
}

/** Smerni krog: nagne se v smer pritiska, puščice se držijo kot na fizičnem daljincu. */
@Composable
fun DPad(s: AppState, diameter: Dp, k: Float) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val rx = remember { Animatable(0f) }
    val ry = remember { Animatable(0f) }
    val okScale = remember { Animatable(1f) }
    var lit by remember { mutableStateOf(-1) } // 0 gor, 1 desno, 2 dol, 3 levo

    Box(
        Modifier
            .size(diameter)
            .graphicsLayer {
                rotationX = rx.value
                rotationY = ry.value
                cameraDistance = 14f * density
            }
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF2E333A), Color(0xFF1F2328))))
            .pointerInput(Unit) {
                val c = size.width / 2f
                detectTapGestures(onPress = { o ->
                    val dx = o.x - c
                    val dy = o.y - c
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    if (hypot(dx, dy) < c * 0.37f) {
                        s.press(23)
                        scope.launch { okScale.animateTo(0.92f, tween(70)) }
                        tryAwaitRelease()
                        scope.launch { okScale.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                    } else {
                        val a = Math.toDegrees(atan2(dy, dx).toDouble())
                        val dir = when {
                            a > -135 && a <= -45 -> 0
                            a > -45 && a <= 45 -> 1
                            a > 45 && a <= 135 -> 2
                            else -> 3
                        }
                        val code = intArrayOf(19, 22, 20, 21)[dir]
                        lit = dir
                        s.keyDown(code)
                        val tx = floatArrayOf(10f, 0f, -10f, 0f)[dir]
                        val ty = floatArrayOf(0f, 10f, 0f, -10f)[dir]
                        scope.launch { rx.animateTo(tx, tween(70)) }
                        scope.launch { ry.animateTo(ty, tween(70)) }
                        tryAwaitRelease()
                        s.keyUp(code)
                        scope.launch { rx.animateTo(0f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                        scope.launch { ry.animateTo(0f, spring(dampingRatio = 0.35f, stiffness = 600f)) }
                        scope.launch {
                            delay(250)
                            if (lit == dir) lit = -1
                        }
                    }
                })
            }
    ) {
        Arrow(Icons.Filled.KeyboardArrowUp, "Gor", lit == 0, Modifier.align(Alignment.TopCenter).padding(top = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowDown, "Dol", lit == 2, Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowLeft, "Levo", lit == 3, Modifier.align(Alignment.CenterStart).padding(start = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowRight, "Desno", lit == 1, Modifier.align(Alignment.CenterEnd).padding(end = 10.dp * k), k)
        Box(
            Modifier
                .align(Alignment.Center)
                .size(diameter * 0.37f)
                .graphicsLayer {
                    scaleX = okScale.value
                    scaleY = okScale.value
                }
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF5B9BFF), Color(0xFF2F6FE4)))),
            contentAlignment = Alignment.Center
        ) {
            Text("OK", color = Color.White, fontSize = (22 * k).sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Lučka, ki utripne ob vsakem pošiljanju, in vrstica stanja. */
@Composable
fun StatusBar(s: AppState) {
    val a = remember { Animatable(0f) }
    val b = remember { Animatable(0f) }
    LaunchedEffect(s.ledTick) {
        if (s.ledTick > 0) {
            a.snapTo(1f)
            a.animateTo(0f, tween(220))
        }
    }
    LaunchedEffect(s.searchOpen, s.searchReady) {
        while (s.searchOpen && !s.searchReady) {
            b.snapTo(1f)
            b.animateTo(0f, tween(160))
            delay(20)
        }
        b.snapTo(0f)
    }
    val v = max(a.value, b.value)

    Row(
        Modifier.fillMaxWidth().padding(start = 12.dp, end = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(lerp(Color(0xFF2A2F36), Color(0xFFFF4D4D), v))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            if (s.connected) "●" else "○",
            color = if (s.connected) GreenColor else DimColor,
            fontSize = 12.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (s.connected) "Povezano  ·  Program ${s.channel}" else s.status.ifEmpty { "Povezujem…" },
            Modifier.weight(1f),
            fontSize = 12.sp,
            color = LabelColor
        )
        IconButton(onClick = { s.showSettings = true }) {
            Icon(Icons.Filled.Settings, contentDescription = "Nastavitve", tint = DimColor)
        }
    }
}

/** Modra lupa, ki se ob tapu razširi v iskalno polje do robov tipk 1 in 3. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(s: AppState, keySize: Dp, gap: Dp, k: Float) {
    val h = 48.dp * k
    val view = LocalView.current
    val focus = remember { FocusRequester() }
    val kb = LocalSoftwareKeyboardController.current
    val fm = LocalFocusManager.current

    var focused by remember { mutableStateOf(false) }

    LaunchedEffect(s.searchOpen) {
        if (s.searchOpen) {
            // počakaj, da se polje razširi, nato poskušaj, dokler polje ne dobi fokusa
            delay(380)
            var tries = 0
            while (!focused && tries < 20) {
                try { focus.requestFocus() } catch (_: Exception) {}
                delay(50)
                tries++
            }
            kb?.show()
        } else {
            fm.clearFocus()
            kb?.hide()
        }
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pa by pulse.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pa"
    )
    val borderColor = when {
        !s.searchOpen -> Color.Transparent
        s.searchReady -> GreenColor.copy(alpha = 0.6f)
        else -> GlowColor.copy(alpha = pa)
    }
    val bg by animateColorAsState(
        if (s.searchOpen) Color(0xFF1A2333) else Color(0xFF2F6FE4),
        tween(300),
        label = "bg"
    )

    BoxWithConstraints(Modifier.fillMaxWidth().height(h)) {
        val col = (maxWidth - gap * 2) / 3
        val edge = (col - keySize) / 2
        val target = if (s.searchOpen) maxWidth - edge * 2 else h
        val w by animateDpAsState(target, tween(350, easing = FastOutSlowInEasing), label = "w")
        val shape = RoundedCornerShape(h / 2)

        Row(
            Modifier
                .offset(x = edge)
                .width(w)
                .height(h)
                .shadow(6.dp, shape)
                .clip(shape)
                .background(bg)
                .border(1.5.dp, borderColor, shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !s.searchOpen
                ) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    s.openSearchUi()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(h), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Išči",
                    modifier = Modifier.size(26.dp * k),
                    tint = if (s.searchOpen) ArrowLit else Color.White
                )
            }
            if (s.searchOpen) {
                Column(Modifier.weight(1f)) {
                    BasicTextField(
                        value = s.searchText,
                        onValueChange = { s.onSearchChange(it) },
                        singleLine = true,
                        textStyle = TextStyle(color = TextColor, fontSize = (17 * k).sp),
                        cursorBrush = SolidColor(ArrowLit),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focus)
                            .onFocusChanged { focused = it.isFocused },
                        decorationBox = { inner ->
                            Box {
                                if (s.searchText.isEmpty()) {
                                    Text("Išči na TV", color = DimColor, fontSize = (17 * k).sp)
                                }
                                inner()
                            }
                        }
                    )
                    Text(
                        if (s.searchReady) "Pripravljeno, tipkaj" else "Odpiram iskanje na TV…",
                        color = if (s.searchReady) GreenColor else DimColor,
                        fontSize = (10 * k).sp
                    )
                }
                Box(
                    Modifier
                        .size(h)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { s.closeSearch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Zapri", tint = DimColor)
                }
            }
        }
    }
}

// ---------- daljinec ----------

@Composable
fun RemoteScreen(s: AppState) {
    BoxWithConstraints(Modifier.fillMaxSize().background(ScreenColor)) {
        val k = minOf(maxHeight / 840.dp, maxWidth / 360.dp).coerceIn(0.7f, 1.2f)
        val key = 58.dp * k
        val big = 62.dp * k
        val small = 52.dp * k
        val gap = 6.dp
        val vgap = 12.dp * k

        Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp)) {
            StatusBar(s)
            Spacer(Modifier.height(4.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(BodyBrush)
                    .padding(horizontal = 14.dp, vertical = 16.dp * k),
                verticalArrangement = Arrangement.spacedBy(vgap)
            ) {
                SearchBar(s, key, gap, k)

                // številčnica
                Column(verticalArrangement = Arrangement.spacedBy(8.dp * k)) {
                    listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(-1, 0, -1)).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                            row.forEach { n ->
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    if (n >= 0) {
                                        RoundKey(key, onDown = { s.digit(n) }) {
                                            Text(
                                                "$n",
                                                fontSize = (30 * k).sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // smerni krog
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    DPad(s, 206.dp * k, k)
                }

                // nazaj, domov
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        RoundKey(big, onDown = { s.press(4) }) { IconLabel(Icons.Filled.ArrowBack, "Nazaj", k) }
                    }
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        RoundKey(big, onDown = { s.press(3) }) { IconLabel(Icons.Filled.Home, "Domov", k) }
                    }
                }

                // glasnost, utišaj, program
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp * k)
                    ) {
                        RoundKey(small, onDown = { s.keyDown(24) }, onUp = { s.keyUp(24) }) {
                            Text("+", fontSize = (28 * k).sp, color = TextColor)
                        }
                        RoundKey(small, onDown = { s.keyDown(25) }, onUp = { s.keyUp(25) }) {
                            Text("−", fontSize = (28 * k).sp, color = TextColor)
                        }
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        RoundKey(big, onDown = { s.press(164) }) {
                            Text("🔇", fontSize = (22 * k).sp)
                        }
                    }
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp * k)
                    ) {
                        RoundKey(small, onDown = { s.channelStep(1) }) {
                            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Naslednji program", modifier = Modifier.size(30.dp * k), tint = TextColor)
                        }
                        RoundKey(small, onDown = { s.channelStep(-1) }) {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Prejšnji program", modifier = Modifier.size(30.dp * k), tint = TextColor)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                    Text("Glasnost", Modifier.weight(1f), fontSize = (11 * k).sp, color = LabelColor, textAlign = TextAlign.Center)
                    Spacer(Modifier.weight(1f))
                    Text("Program", Modifier.weight(1f), fontSize = (11 * k).sp, color = LabelColor, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
