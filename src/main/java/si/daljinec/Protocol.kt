package si.daljinec

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Build
import android.media.AudioAttributes
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder

// ---------- kode tipk in časi ----------

/** Kode tipk, ki jih pošiljamo boxu (standardne Android kode tipk). */
object Key {
    const val HOME = KeyEvent.KEYCODE_HOME
    const val BACK = KeyEvent.KEYCODE_BACK
    const val UP = KeyEvent.KEYCODE_DPAD_UP
    const val DOWN = KeyEvent.KEYCODE_DPAD_DOWN
    const val LEFT = KeyEvent.KEYCODE_DPAD_LEFT
    const val RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT
    const val OK = KeyEvent.KEYCODE_DPAD_CENTER
    const val VOL_UP = KeyEvent.KEYCODE_VOLUME_UP
    const val VOL_DOWN = KeyEvent.KEYCODE_VOLUME_DOWN
    const val MUTE = KeyEvent.KEYCODE_VOLUME_MUTE
    const val POWER = KeyEvent.KEYCODE_POWER
    /** Številka n ima kodo DIGIT_0 + n. */
    const val DIGIT_0 = KeyEvent.KEYCODE_0
}

/** Kako se tipka pošlje boxu (polje "direction" v protokolu). */
object KeyDir {
    const val DOWN = 1   // pritisnjena (držanje)
    const val UP = 2     // spuščena
    const val SHORT = 3  // kratek pritisk
}

/** Časi v milisekundah. */
object Timing {
    /** Premor med koraki po menijih boxa. */
    const val MENU_STEP_MS = 250L
    /** Premor pred OK, da se izbira v meniju umiri. */
    const val SETTLE_BEFORE_OK_MS = 600L
    /** Premor med števkami pri vnosu številke programa. */
    const val DIGIT_GAP_MS = 100L
    /** Dolg pritisk na glasnost − utiša zvok. */
    const val LONG_PRESS_MUTE_MS = 600L
    /** Ponovni poskus povezave po prekinitvi. */
    const val RECONNECT_DELAY_MS = 800L
    /** Pritiski glasnosti, ki čakajo na povezavo, veljajo tako dolgo. */
    const val QUEUE_MAX_AGE_MS = 4000L
    /** Premor med čakajočimi pritiski glasnosti ob povezavi. */
    const val VOLUME_REPLAY_GAP_MS = 80L
    /** Brez povezave toliko časa: "Box ni dosegljiv". */
    const val UNREACHABLE_AFTER_MS = 4000L
    /** Če box toliko časa ne pošlje ničesar, je povezava verjetno mrtva. */
    const val STALE_CONNECTION_MS = 15000L
    /** Tipka ostane vidno pogreznjena vsaj toliko časa. */
    const val MIN_PRESS_VISIBLE_MS = 110L
    /** Nasvet se pokaže toliko po povezavi. */
    const val TIP_DELAY_MS = 1500L
}

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

    /** Naključna oznaka tega telefona (ustvari se enkrat). */
    fun deviceId(ctx: Context): String {
        val p = ctx.getSharedPreferences("daljinec", Context.MODE_PRIVATE)
        p.getString("devid", null)?.let { return it }
        val id = java.util.UUID.randomUUID().toString().take(6)
        p.edit().putString("devid", id).apply()
        return id
    }

    /** Ime, s katerim se telefon predstavi boxu: vsak telefon ima svoje. */
    fun clientName(ctx: Context): String = "Daljinec ${Build.MODEL} ${deviceId(ctx)}"

    fun load(ctx: Context): KeyStore {
        // novo potrdilo z imenom, ki je za vsak telefon drugačno
        val file = File(ctx.filesDir, "client2.p12")
        val ks = KeyStore.getInstance("PKCS12")
        if (file.exists()) {
            file.inputStream().use { ks.load(it, PASS) }
            return ks
        }
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        val name = X500Name("CN=daljinec-" + deviceId(ctx))
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

class Pairing(ctx: Context, private val host: String, private val tr: Tr) {
    private val ks = Identity.load(ctx)
    private val clientName = Identity.clientName(ctx)
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
        val resp = Frames.read(inp) ?: throw IllegalStateException(tr.connLost)
        val r = PbReader(resp)
        val st = r.long(2) ?: 200L
        if (st != 200L) throw IllegalStateException("${tr.pairRejected} ($st)")
        return r
    }

    fun begin() {
        val s = ssl.socketFactory.createSocket(host, 6467) as SSLSocket
        s.startHandshake()
        socket = s
        inp = s.inputStream
        out = s.outputStream
        exchange(frame { msg(10, PbWriter().str(1, "atvremote").str(2, clientName)) })
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
        require(c.length == 6 && c.all { it in "0123456789ABCDEF" }) { tr.codeFormat }
        val clientKey = ks.getCertificate("client").publicKey as RSAPublicKey
        val serverKey = socket!!.session.peerCertificates[0].publicKey as RSAPublicKey
        val md = MessageDigest.getInstance("SHA-256")
        md.update(unsigned(clientKey.modulus))
        md.update(unsigned(clientKey.publicExponent))
        md.update(unsigned(serverKey.modulus))
        md.update(unsigned(serverKey.publicExponent))
        md.update(hex(c.substring(2)))
        val hash = md.digest()
        require(hash[0] == c.substring(0, 2).toInt(16).toByte()) { tr.wrongCode }
        exchange(frame { msg(40, PbWriter().bytes(1, hash)) })
        close()
    }

    fun close() {
        val s = socket ?: return
        Thread { try { s.close() } catch (_: Exception) {} }.start()
    }
}

// ---------- daljinec (vrata 6466) ----------

class RemoteConnection(
    private val ctx: Context,
    private val host: String,
    private val onState: (Boolean) -> Unit
) {
    @Volatile private var socket: SSLSocket? = null
    /** Kdaj je box nazadnje kaj poslal (za preverjanje, ali povezava še živi). */
    @Volatile var lastRx = System.currentTimeMillis()
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
                lastRx = System.currentTimeMillis()
                val r = PbReader(data)
                when {
                    r.has(1) -> send(
                        PbWriter().msg(
                            1,
                            PbWriter().int(1, 622).msg(
                                2,
                                PbWriter()
                                    .str(1, Identity.clientName(ctx)).str(2, Build.MANUFACTURER)
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

    fun key(code: Int, dir: Int = KeyDir.SHORT) = send(PbWriter().msg(10, PbWriter().int(1, code).int(2, dir)))

    /** Lepo zapre povezavo (na ločeni niti, ker zapiranje pošlje boxu sporočilo). */
    fun close() {
        val s = socket ?: return
        Thread { try { s.close() } catch (_: Exception) {} }.start()
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
                // novejši Android lahko najprej vrne IPv6 naslov; vzamemo IPv4, če obstaja
                val addrs: List<java.net.InetAddress> =
                    if (Build.VERSION.SDK_INT >= 34) serviceInfo.hostAddresses
                    else listOfNotNull(serviceInfo.host)
                val ip = addrs.firstOrNull { it is java.net.Inet4Address } ?: addrs.firstOrNull()
                onFound(serviceInfo.serviceName, ip?.hostAddress ?: "")
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
