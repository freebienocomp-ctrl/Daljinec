package si.daljinec

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Build
import android.media.AudioAttributes
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
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
val PowerBrush = Brush.verticalGradient(listOf(Color(0xFFE5484D), Color(0xFFB4232A)))

// ---------- jezik aplikacije ----------

/** Jezik telefona: sl, sr (tudi hr, bs, me) ali en. */
fun systemLang(): String = when (java.util.Locale.getDefault().language) {
    "sl" -> "sl"
    "sr", "hr", "bs", "sh", "me", "cnr" -> "sr"
    else -> "en"
}

class Tr(
    val setupTitle: String,
    val setupInfo: String,
    val foundBoxes: String,
    val ipLabel: String,
    val pair: String,
    val codeLabel: String,
    val confirmCode: String,
    val back: String,
    val connecting: String,
    val enterCode: String,
    val error: String,
    val noConnection: String,
    val connLost: String,
    val pairRejected: String,
    val codeFormat: String,
    val wrongCode: String,
    val kbTitle: String,
    val kbExplain: String,
    val step1: String,
    val step2: String,
    val step2Text: (String) -> String,
    val openLang: String,
    val notConnected: String,
    val done: String,
    val up: String,
    val down: String,
    val left: String,
    val right: String,
    val settings: String,
    val chTitle: String,
    val syncNever: String,
    val syncLast: (String, Int) -> String,
    val syncRunning: String,
    val syncFailed: String,
    val syncNow: String,
    val setChTitle: String,
    val setChLabel: String,
    val ok: String,
    val cancel: String,
    val stepLabel: String,
    val stepHint: String,
    val homeLabel: String,
    val homeHint: String,
    val screenLabel: String,
    val screenHint: String,
    val kbMustMatch: String,
    val kbSetOnBox: String,
    val appLang: String,
    val auto: String,
    val vibrate: String,
    val save: String,
    val testTitle: String,
    val testCode: String,
    val send: String,
    val sendHold: String,
    val repair: String,
    val pairBox: String,
    val home: String,
    val search: String,
    val volume: String,
    val channel: String,
    val nextCh: String,
    val prevCh: String,
    val notPaired: String,
    val connectedCh: (Int) -> String,
    val searchHint: String,
    val tapToType: String,
    val ready: String,
    val openingSearch: String,
    val close: String
)

val TR = mapOf(
    "sl" to Tr(
        setupTitle = "Povezava z EON boxom",
        setupInfo = "Telefon in box morata biti v istem Wi-Fi omrežju, box pa vklopljen.",
        foundBoxes = "Najdeni boxi:",
        ipLabel = "IP naslov boxa",
        pair = "Upari",
        codeLabel = "Koda s TV-ja (6 znakov)",
        confirmCode = "Potrdi kodo",
        back = "Nazaj",
        connecting = "Povezujem…",
        enterCode = "Vpiši kodo, ki je prikazana na TV-ju.",
        error = "Napaka",
        noConnection = "Ni povezave",
        connLost = "Povezava prekinjena",
        pairRejected = "Box je zavrnil uparjanje",
        codeFormat = "Koda ima 6 znakov (0-9, A-F)",
        wrongCode = "Napačna koda",
        kbTitle = "Jezik tipkovnice za iskanje",
        kbExplain = "Jezik v aplikaciji in jezik tipkovnice v iskanju na boxu se morata ujemati, sicer se vpisujejo napačne črke. Nastaviš samo enkrat.",
        step1 = "1. V aplikaciji",
        step2 = "2. Na boxu",
        step2Text = { n -> "Tapni gumb spodaj. Na TV-ju se odpre seznam jezikov. S tipkami ▲ ▼ izberi $n in potrdi z OK." },
        openLang = "Odpri izbiro jezika na TV",
        notConnected = "Box ni povezan",
        done = "Končano",
        up = "Gor",
        down = "Dol",
        left = "Levo",
        right = "Desno",
        settings = "Nastavitve",
        chTitle = "Seznam programov",
        syncNever = "Seznam programov še ni bil prenesen.",
        syncLast = { d, n -> "Zadnja sinhronizacija: $d · $n pozicij" },
        syncRunning = "Prenašam seznam programov…",
        syncFailed = "Zadnji poskus ni uspel, uporabljam prejšnji seznam.",
        syncNow = "Posodobi zdaj",
        setChTitle = "Na katerem programu si?",
        setChLabel = "Številka programa",
        ok = "V redu",
        cancel = "Prekliči",
        stepLabel = "Premor med koraki pri iskanju (ms)",
        stepHint = "Manjše število = hitrejše tipkanje (5–1000). Če črke manjkajo, ga povečaj.",
        homeLabel = "Čakanje po Domov (ms)",
        homeHint = "Koliko časa aplikacija počaka po Domov, preden pošlje ◀ (50–5000). Če iskanje ne odpre, ga povečaj.",
        screenLabel = "Čakanje na odprtje iskanja (ms)",
        screenHint = "Koliko časa aplikacija počaka po ◀, da EON odpre iskanje (100–5000). Če se prva črka ne vpiše, ga povečaj.",
        kbMustMatch = "Mora se ujemati z jezikom tipkovnice v iskanju na boxu.",
        kbSetOnBox = "Nastavi jezik tudi na boxu",
        appLang = "Jezik aplikacije",
        auto = "Samodejno",
        vibrate = "Vibriranje ob pritisku",
        save = "Shrani",
        testTitle = "Preizkus kode tipke",
        testCode = "Test koda",
        send = "Pošlji",
        sendHold = "Pošlji (drži)",
        repair = "Ponovno upari box",
        pairBox = "Upari box",
        home = "Domov",
        search = "Išči",
        volume = "Glasnost",
        channel = "Program",
        nextCh = "Naslednji program",
        prevCh = "Prejšnji program",
        notPaired = "Box ni uparjen  ·  Poveži",
        connectedCh = { n -> "Povezano  ·  Program $n" },
        searchHint = "Išči na TV",
        tapToType = "Tapni sem za tipkanje",
        ready = "Pripravljeno, tipkaj",
        openingSearch = "Odpiram iskanje na TV…",
        close = "Zapri"
    ),
    "en" to Tr(
        setupTitle = "Connect to EON box",
        setupInfo = "The phone and the box must be on the same Wi-Fi network, and the box must be on.",
        foundBoxes = "Boxes found:",
        ipLabel = "Box IP address",
        pair = "Pair",
        codeLabel = "Code from TV (6 characters)",
        confirmCode = "Confirm code",
        back = "Back",
        connecting = "Connecting…",
        enterCode = "Enter the code shown on the TV.",
        error = "Error",
        noConnection = "No connection",
        connLost = "Connection lost",
        pairRejected = "The box rejected pairing",
        codeFormat = "The code has 6 characters (0-9, A-F)",
        wrongCode = "Wrong code",
        kbTitle = "Search keyboard language",
        kbExplain = "The language in the app and the keyboard language in the box search must match, otherwise wrong letters are typed. You only set this once.",
        step1 = "1. In the app",
        step2 = "2. On the box",
        step2Text = { n -> "Tap the button below. A list of languages opens on the TV. Use ▲ ▼ to select $n and confirm with OK." },
        openLang = "Open language list on TV",
        notConnected = "Box not connected",
        done = "Done",
        up = "Up",
        down = "Down",
        left = "Left",
        right = "Right",
        settings = "Settings",
        chTitle = "Channel list",
        syncNever = "The channel list hasn't been downloaded yet.",
        syncLast = { d, n -> "Last sync: $d · $n positions" },
        syncRunning = "Downloading channel list…",
        syncFailed = "Last attempt failed, using the previous list.",
        syncNow = "Update now",
        setChTitle = "Which channel are you on?",
        setChLabel = "Channel number",
        ok = "OK",
        cancel = "Cancel",
        stepLabel = "Delay between search steps (ms)",
        stepHint = "Lower = faster typing (5–1000). If letters are missing, increase it.",
        homeLabel = "Wait after Home (ms)",
        homeHint = "How long the app waits after Home before sending ◀ (50–5000). If search doesn't open, increase it.",
        screenLabel = "Wait for search to open (ms)",
        screenHint = "How long the app waits after ◀ for EON to open search (100–5000). If the first letter is missing, increase it.",
        kbMustMatch = "Must match the keyboard language in the box search.",
        kbSetOnBox = "Set the language on the box too",
        appLang = "App language",
        auto = "Automatic",
        vibrate = "Vibrate on press",
        save = "Save",
        testTitle = "Key code test",
        testCode = "Test code",
        send = "Send",
        sendHold = "Send (hold)",
        repair = "Pair box again",
        pairBox = "Pair box",
        home = "Home",
        search = "Search",
        volume = "Volume",
        channel = "Channel",
        nextCh = "Next channel",
        prevCh = "Previous channel",
        notPaired = "Box not paired  ·  Connect",
        connectedCh = { n -> "Connected  ·  Channel $n" },
        searchHint = "Search on TV",
        tapToType = "Tap here to type",
        ready = "Ready, start typing",
        openingSearch = "Opening search on TV…",
        close = "Close"
    ),
    "sr" to Tr(
        setupTitle = "Povezivanje sa EON boksom",
        setupInfo = "Telefon i boks moraju biti na istoj Wi-Fi mreži, a boks mora biti uključen.",
        foundBoxes = "Pronađeni boksovi:",
        ipLabel = "IP adresa boksa",
        pair = "Upari",
        codeLabel = "Kod sa TV-a (6 znakova)",
        confirmCode = "Potvrdi kod",
        back = "Nazad",
        connecting = "Povezivanje…",
        enterCode = "Unesi kod koji je prikazan na TV-u.",
        error = "Greška",
        noConnection = "Nema veze",
        connLost = "Veza je prekinuta",
        pairRejected = "Boks je odbio uparivanje",
        codeFormat = "Kod ima 6 znakova (0-9, A-F)",
        wrongCode = "Pogrešan kod",
        kbTitle = "Jezik tastature za pretragu",
        kbExplain = "Jezik u aplikaciji i jezik tastature u pretrazi na boksu moraju da se poklapaju, inače se unose pogrešna slova. Podešava se samo jednom.",
        step1 = "1. U aplikaciji",
        step2 = "2. Na boksu",
        step2Text = { n -> "Dodirni dugme ispod. Na TV-u se otvara lista jezika. Tasterima ▲ ▼ izaberi $n i potvrdi sa OK." },
        openLang = "Otvori izbor jezika na TV-u",
        notConnected = "Boks nije povezan",
        done = "Gotovo",
        up = "Gore",
        down = "Dole",
        left = "Levo",
        right = "Desno",
        settings = "Podešavanja",
        chTitle = "Lista kanala",
        syncNever = "Lista kanala još nije preuzeta.",
        syncLast = { d, n -> "Poslednja sinhronizacija: $d · $n pozicija" },
        syncRunning = "Preuzimam listu kanala…",
        syncFailed = "Poslednji pokušaj nije uspeo, koristim prethodnu listu.",
        syncNow = "Ažuriraj sada",
        setChTitle = "Na kom si kanalu?",
        setChLabel = "Broj kanala",
        ok = "U redu",
        cancel = "Otkaži",
        stepLabel = "Pauza između koraka pri pretrazi (ms)",
        stepHint = "Manji broj = brže kucanje (5–1000). Ako slova nedostaju, povećaj ga.",
        homeLabel = "Čekanje posle Početne (ms)",
        homeHint = "Koliko aplikacija čeka posle Početne pre nego što pošalje ◀ (50–5000). Ako se pretraga ne otvori, povećaj ga.",
        screenLabel = "Čekanje na otvaranje pretrage (ms)",
        screenHint = "Koliko aplikacija čeka posle ◀ da EON otvori pretragu (100–5000). Ako se prvo slovo ne unese, povećaj ga.",
        kbMustMatch = "Mora da se poklapa sa jezikom tastature u pretrazi na boksu.",
        kbSetOnBox = "Podesi jezik i na boksu",
        appLang = "Jezik aplikacije",
        auto = "Automatski",
        vibrate = "Vibracija pri pritisku",
        save = "Sačuvaj",
        testTitle = "Test koda tastera",
        testCode = "Test kod",
        send = "Pošalji",
        sendHold = "Pošalji (drži)",
        repair = "Ponovo upari boks",
        pairBox = "Upari boks",
        home = "Početna",
        search = "Traži",
        volume = "Jačina",
        channel = "Kanal",
        nextCh = "Sledeći kanal",
        prevCh = "Prethodni kanal",
        notPaired = "Boks nije uparen  ·  Poveži",
        connectedCh = { n -> "Povezano  ·  Kanal $n" },
        searchHint = "Traži na TV-u",
        tapToType = "Dodirni ovde za kucanje",
        ready = "Spremno, kucaj",
        openingSearch = "Otvaram pretragu na TV-u…",
        close = "Zatvori"
    )
)

// ---------- vibriranje (neodvisno od nastavitve tipkovnice) ----------

object Haptics {
    var enabled = true

    fun tick(view: View) {
        if (!enabled) return
        try {
            val ctx = view.context
            val vib: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                ctx.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            val amp = if (vib.hasAmplitudeControl()) 140 else VibrationEffect.DEFAULT_AMPLITUDE
            val effect = VibrationEffect.createOneShot(18, amp)
            // kot alarm: deluje tudi, če je "vibriranje ob dotiku" v telefonu izklopljeno
            if (Build.VERSION.SDK_INT >= 33) {
                vib.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(
                    effect,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
        } catch (_: Exception) {}
    }
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

class Pairing(ctx: Context, private val host: String, private val tr: Tr) {
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

// ---------- EON tipkovnica ----------

/** Vrstice 1-5 EON tipkovnice za vsak jezik, vsaka ima 6 stolpcev.
 *  Pri srbski so Dž, Lj in Nj označene s posebnimi znaki (aplikacija jih ne uporablja,
 *  "lj" vpiše kot L + J). */
val EON_LAYOUTS = mapOf(
    "sl" to listOf("ABCČDE", "FGHIJK", "LMNOPR", "SŠTUVZ", "Ž:;.,-"),
    "en" to listOf("ABCDEF", "GHIJKL", "MNOPQR", "STUVWX", "YZ.,-@"),
    "sr" to listOf("ABCČĆD", "\u0001ĐEFGH", "IJKL\u0002M", "N\u0003OPRS", "ŠTUVZŽ")
)

val LANG_NAMES = listOf("sl" to "Slovenščina", "en" to "English", "sr" to "Srpski")

/** Če črke ni na tipkovnici, poskusi podobno (npr. Č -> C v angleščini). */
private val EON_ALT = mapOf(
    'Ć' to "ČC",
    'Č' to "C",
    'Đ' to "D",
    'Š' to "S",
    'Ž' to "Z"
)

/** Vrne (vrstica, stolpec) za znak, (-1, -1) za presledek ali null, če znaka ni. */
fun eonPos(ch: Char, lang: String): Pair<Int, Int>? {
    val rows = EON_LAYOUTS[lang] ?: EON_LAYOUTS.getValue("sl")
    val u = ch.uppercaseChar()
    if (u == ' ') return Pair(-1, -1)
    for (c in "$u" + (EON_ALT[u] ?: "")) {
        rows.forEachIndexed { r, row ->
            val col = row.indexOf(c)
            if (col >= 0) return Pair(r + 1, col)
        }
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
    var positions by mutableStateOf(
        (prefs.getString("chpos", "") ?: "").split(',').mapNotNull { it.trim().toIntOrNull() }
    )
    var lastSync by mutableStateOf(prefs.getLong("chsync", 0L))
    var syncRunning by mutableStateOf(false)
    var syncFailed by mutableStateOf(false)
    var showChannelDialog by mutableStateOf(false)
    /** Nastavi MainActivity: zažene branje programske sheme v ozadju. */
    var syncRunner: (() -> Unit)? = null
    var stepDelay by mutableStateOf(prefs.getInt("step", 30))
    var homeWait by mutableStateOf(prefs.getInt("home", 500))
    var screenWait by mutableStateOf(prefs.getInt("screen", 1000))
    var showSettings by mutableStateOf(false)
    var showSetup by mutableStateOf(false)
    var searchText by mutableStateOf("")
    var searchOpen by mutableStateOf(false)
    var searchReady by mutableStateOf(false)
    var ledTick by mutableStateOf(0)
    var vibrate by mutableStateOf(prefs.getBoolean("vib", true))
    var kbLang by mutableStateOf(prefs.getString("kblang", null) ?: systemLang())
    var uiLangPref by mutableStateOf(prefs.getString("uilang", "auto") ?: "auto")
    val tr: Tr get() = TR.getValue(if (uiLangPref == "auto") systemLang() else uiLangPref)
    var showLangSetup by mutableStateOf(paired && !prefs.getBoolean("langdone", false))
    val found = mutableStateListOf<Pair<String, String>>()

    init {
        Haptics.enabled = vibrate
        // enkratna zamenjava stare privzete vrednosti 80 ms s 30 ms
        if (!prefs.getBoolean("step30", false)) {
            if (prefs.getInt("step", 30) == 80) {
                prefs.edit().putInt("step", 30).apply()
                stepDelay = 30
            }
            prefs.edit().putBoolean("step30", true).apply()
        }
    }

    fun saveUiLang(l: String) {
        uiLangPref = l
        prefs.edit().putString("uilang", l).apply()
    }

    fun saveKbLang(l: String) {
        kbLang = l
        prefs.edit().putString("kblang", l).apply()
    }

    /** Končano na zaslonu za jezik. */
    fun finishLangSetup() {
        prefs.edit().putBoolean("langdone", true).apply()
        showLangSetup = false
    }

    /** Odpre iskanje na TV-ju in pritisne gumb za jezik, da se pokaže seznam jezikov. */
    fun openLangOnTv() {
        closeSearch()
        ledTick++
        keys.execute {
            kbOpen = false
            openSearchIfNeeded()   // izbrana je črka A
            step(19, 250)          // gor -> mikrofon
            step(22, 250)          // desno -> jezik
            step(23)               // OK -> seznam jezikov
            kbOpen = false         // po izbiri jezika začnemo iskanje znova
        }
    }

    fun saveVibrate(on: Boolean) {
        vibrate = on
        Haptics.enabled = on
        prefs.edit().putBoolean("vib", on).apply()
    }

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
        status = tr.connecting
        net.execute {
            try {
                pairing?.close()
                val p = Pairing(ctx, host, tr)
                p.begin()
                pairing = p
                waitingCode = true
                status = tr.enterCode
            } catch (e: Exception) {
                status = "${tr.error}: ${e.message}"
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
                showSetup = false
                if (!prefs.getBoolean("langdone", false)) showLangSetup = true
                status = ""
                connect()
            } catch (e: Exception) {
                status = "${tr.error}: ${e.message}"
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
        showSetup = true
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
                    status = "${tr.noConnection}: ${e.message}"
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

    fun saveTimings(step: Int, home: Int, screen: Int) {
        stepDelay = step
        homeWait = home
        screenWait = screen
        prefs.edit().putInt("step", step).putInt("home", home).putInt("screen", screen).apply()
    }

    /** Ročni popravek: na katerem programu je box zdaj. */
    fun setChannel(c: Int) {
        channel = c
        prefs.edit().putInt("channel", c).apply()
    }

    /** Ali je čas za dnevno osvežitev seznama programov. */
    fun needsSync(): Boolean = System.currentTimeMillis() - lastSync > 24L * 3600 * 1000

    /** Konec branja programske sheme (null = ni uspelo). */
    fun syncDone(list: List<Int>?) {
        syncRunning = false
        if (list != null && list.size >= 20) {
            val sorted = list.distinct().sorted()
            positions = sorted
            lastSync = System.currentTimeMillis()
            syncFailed = false
            prefs.edit()
                .putString("chpos", sorted.joinToString(","))
                .putLong("chsync", lastSync)
                .apply()
        } else {
            syncFailed = true
        }
    }

    /** Številka s številčnice: pošlje tipko in si zapomni program. */
    fun digit(n: Int) {
        val now = System.currentTimeMillis()
        entry = if (now - lastDigit < 2000 && entry.length < 4) entry + n else n.toString()
        lastDigit = now
        entry.toIntOrNull()?.let { if (it > 0) setChannel(it) }
        press(7 + n)
    }

    /** Program ▲ / ▼: vtipka naslednjo obstoječo pozicijo in potrdi z OK. */
    fun channelStep(d: Int) {
        val list = positions
        val c = when {
            list.isEmpty() -> (channel + d).coerceAtLeast(1)
            d > 0 -> list.firstOrNull { it > channel } ?: list.first()
            else -> list.lastOrNull { it < channel } ?: list.last()
        }
        setChannel(c)
        entry = ""
        closeSearch()
        ledTick++
        keys.execute {
            for (ch in c.toString()) {
                remote?.key(7 + (ch - '0'))
                Thread.sleep(100)
            }
            remote?.key(23)
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
        val lang = kbLang
        val delCount = old.substring(common).count { eonPos(it, lang) != null }
        val add = n.substring(common).mapNotNull { eonPos(it, lang) }
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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        state = AppState(applicationContext)
        state.syncRunner = { runSync() }
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
        if (state.needsSync()) runSync()
    }

    override fun onStop() {
        super.onStop()
        state.disconnect()
    }

    override fun onDestroy() {
        stopSync(null)
        super.onDestroy()
    }

    // ---------- branje programske sheme v ozadju (nevidni brskalnik) ----------

    private var web: WebView? = null
    private val syncHandler = Handler(Looper.getMainLooper())
    private var best: List<Int> = emptyList()

    private fun stopSync(result: List<Int>?) {
        val w = web ?: return
        web = null
        syncHandler.removeCallbacksAndMessages(null)
        try {
            (w.parent as? ViewGroup)?.removeView(w)
            w.destroy()
        } catch (_: Exception) {}
        state.syncDone(result)
    }

    private fun runSync() {
        if (web != null) return
        state.syncRunning = true
        best = emptyList()
        val w = WebView(this)
        web = w
        w.alpha = 0f
        addContentView(w, ViewGroup.LayoutParams(1, 1))
        w.settings.javaScriptEnabled = true
        w.settings.domStorageEnabled = true

        var started = false
        var iter = 0
        var lastCount = -1
        var stable = 0
        lateinit var tick: Runnable
        tick = Runnable {
            if (web !== w) return@Runnable
            iter++
            w.evaluateJavascript(SYNC_JS) { res ->
                var more = false
                try {
                    val json = org.json.JSONObject(org.json.JSONTokener(res).nextValue() as String)
                    more = json.optBoolean("more")
                    val arr = json.optJSONArray("list")
                    val list = mutableListOf<Int>()
                    if (arr != null) for (i in 0 until arr.length()) list.add(arr.getInt(i))
                    if (list.size >= best.size) best = list
                } catch (_: Exception) {}
                if (!more && best.size == lastCount) stable++ else stable = 0
                lastCount = best.size
                if ((stable >= 2 && (best.isNotEmpty() || iter >= 10)) || iter >= 60) stopSync(best) else syncHandler.postDelayed(tick, 1500)
            }
        }
        w.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (!started) {
                    started = true
                    syncHandler.postDelayed(tick, 2500)
                }
            }
        }
        // varovalo: najdlje 2 minuti
        syncHandler.postDelayed({ if (web === w) stopSync(best) }, 120_000)
        w.loadUrl("https://sl.eon.tv/programska-shema/")
    }
}

/** Na strani klikne "Prikaži več" (če obstaja) in prebere vse pozicije programov. */
private const val SYNC_JS = """
(function(){
  var btn = null;
  var els = document.querySelectorAll('button,a,span,div');
  for (var i = 0; i < els.length; i++) {
    var e = els[i];
    if (e.children.length > 2) continue;
    var tx = (e.innerText || '').trim().toLowerCase();
    if (tx.length < 30 && (tx.indexOf('prikaži več') == 0 || tx.indexOf('prikazi vec') == 0 || tx.indexOf('prikaži vec') == 0) && e.offsetParent !== null) { btn = e; break; }
  }
  var set = {};
  var cells = document.querySelectorAll('[data-label]');
  for (var a = 0; a < cells.length; a++) {
    if (/pozicija/i.test(cells[a].getAttribute('data-label'))) {
      var n1 = parseInt((cells[a].innerText || '').replace(/\D/g, ''));
      if (n1 > 0) set[n1] = 1;
    }
  }
  var tables = document.querySelectorAll('table');
  for (var b = 0; b < tables.length; b++) {
    var hs = tables[b].querySelectorAll('th'), idx = -1;
    for (var j = 0; j < hs.length; j++) if (/pozicija/i.test(hs[j].innerText)) idx = j;
    if (idx >= 0) {
      var rows = tables[b].querySelectorAll('tr');
      for (var r = 0; r < rows.length; r++) {
        var c = rows[r].children[idx];
        if (c && c.tagName == 'TD') {
          var n2 = parseInt((c.innerText || '').replace(/\D/g, ''));
          if (n2 > 0) set[n2] = 1;
        }
      }
    }
  }
  var re = /Pozicija\s*:?\s*(\d{1,4})/gi, m, txt = document.body.innerText;
  while ((m = re.exec(txt)) !== null) set[parseInt(m[1])] = 1;
  var list = Object.keys(set).map(Number);
  if (btn) btn.click();
  return JSON.stringify({ more: !!btn, list: list });
})()
"""

@Composable
fun App(s: AppState) {
    when {
        s.showSetup -> SetupScreen(s)
        s.showLangSetup -> LangScreen(s)
        s.showSettings -> SettingsScreen(s)
        else -> RemoteScreen(s)
    }
}

// ---------- uparjanje ----------

@Composable
fun SetupScreen(s: AppState) {
    val ctx = LocalContext.current
    val t = s.tr
    var code by remember { mutableStateOf("") }

    BackHandler { s.showSetup = false }

    DisposableEffect(Unit) {
        val d = Discovery(ctx) { name, ip -> s.addFound(name, ip) }
        d.start()
        onDispose { d.stop() }
    }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(t.setupTitle, style = MaterialTheme.typography.titleLarge)
        Text(t.setupInfo)
        if (s.found.isNotEmpty()) {
            Text(t.foundBoxes)
            s.found.forEach { (name, ip) ->
                OutlinedButton(onClick = { s.host = ip }, modifier = Modifier.fillMaxWidth()) {
                    Text("$name ($ip)")
                }
            }
        }
        OutlinedTextField(
            value = s.host,
            onValueChange = { s.host = it.trim() },
            label = { Text(t.ipLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { s.startPairing() },
            enabled = s.host.isNotEmpty() && !s.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.pair) }

        if (s.waitingCode) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase().take(6) },
                label = { Text(t.codeLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { s.finishPairing(code) },
                enabled = code.length == 6 && !s.busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(t.confirmCode) }
        }
        if (s.status.isNotEmpty()) Text(s.status)
        OutlinedButton(
            onClick = { s.showSetup = false },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.back) }
    }
}

// ---------- jezik tipkovnice ----------

/** Vrsta okroglih gumbov za izbiro (izbrani je obarvan). */
@Composable
fun Choices(options: List<Pair<String, String>>, selected: String, perRow: Int, onPick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(perRow).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (code, name) ->
                    val on = selected == code
                    Box(
                        Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (on) Color(0xFF16233A) else Color.Transparent)
                            .border(1.5.dp, if (on) AccentColor else Color(0xFF3A3F46), RoundedCornerShape(22.dp))
                            .clickable { onPick(code) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name, fontSize = 14.sp, color = TextColor)
                    }
                }
            }
        }
    }
}

@Composable
fun LangPicker(s: AppState) {
    Choices(LANG_NAMES, s.kbLang, 3) { s.saveKbLang(it) }
}

@Composable
fun LangScreen(s: AppState) {
    BackHandler { s.finishLangSetup() }
    val t = s.tr
    val name = LANG_NAMES.firstOrNull { it.first == s.kbLang }?.second ?: "Slovenščina"

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(t.kbTitle, style = MaterialTheme.typography.titleLarge)
        Text(t.kbExplain, color = LabelColor)
        Text(t.step1, color = GlowColor, fontWeight = FontWeight.Medium)
        LangPicker(s)
        Text(t.step2, color = GlowColor, fontWeight = FontWeight.Medium)
        Text(t.step2Text(name), color = LabelColor)
        OutlinedButton(
            onClick = { s.openLangOnTv() },
            enabled = s.connected,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (s.connected) t.openLang else t.notConnected) }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            RoundKey(52.dp, onDown = { s.press(19) }) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = t.up, modifier = Modifier.size(28.dp), tint = TextColor)
            }
            RoundKey(52.dp, onDown = { s.press(23) }, brush = Brush.verticalGradient(listOf(Color(0xFF5B9BFF), Color(0xFF2F6FE4)))) {
                Text("OK", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            RoundKey(52.dp, onDown = { s.press(20) }) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = t.down, modifier = Modifier.size(28.dp), tint = TextColor)
            }
        }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = { s.finishLangSetup() },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.done) }
    }
}

// ---------- nastavitve ----------

@Composable
fun SettingsScreen(s: AppState) {
    var stepText by remember { mutableStateOf(s.stepDelay.toString()) }
    var homeText by remember { mutableStateOf(s.homeWait.toString()) }
    var screenText by remember { mutableStateOf(s.screenWait.toString()) }
    var testCode by remember { mutableStateOf("") }
    val t = s.tr

    BackHandler { s.showSettings = false }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(t.settings, style = MaterialTheme.typography.titleLarge)
        Text(t.appLang, style = MaterialTheme.typography.titleMedium)
        Choices(listOf("auto" to t.auto) + LANG_NAMES, s.uiLangPref, 2) { s.saveUiLang(it) }
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = stepText,
            onValueChange = { stepText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text(t.stepLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(t.stepHint, color = DimColor, fontSize = 13.sp)
        OutlinedTextField(
            value = homeText,
            onValueChange = { homeText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text(t.homeLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(t.homeHint, color = DimColor, fontSize = 13.sp)
        OutlinedTextField(
            value = screenText,
            onValueChange = { screenText = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text(t.screenLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(t.screenHint, color = DimColor, fontSize = 13.sp)
        Text(t.chTitle, style = MaterialTheme.typography.titleMedium)
        Text(
            when {
                s.syncRunning -> t.syncRunning
                s.lastSync == 0L -> t.syncNever
                else -> t.syncLast(
                    java.text.SimpleDateFormat("d. M. yyyy HH:mm", java.util.Locale.getDefault())
                        .format(java.util.Date(s.lastSync)),
                    s.positions.size
                )
            },
            color = LabelColor, fontSize = 14.sp
        )
        if (s.syncFailed && !s.syncRunning) Text(t.syncFailed, color = Color(0xFFFBBF24), fontSize = 13.sp)
        OutlinedButton(
            onClick = { s.syncRunner?.invoke() },
            enabled = !s.syncRunning,
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.syncNow) }
        Text(t.kbTitle, style = MaterialTheme.typography.titleMedium)
        Text(t.kbMustMatch, color = DimColor, fontSize = 13.sp)
        LangPicker(s)
        OutlinedButton(
            onClick = { s.showLangSetup = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.kbSetOnBox) }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.vibrate, Modifier.weight(1f))
            Switch(checked = s.vibrate, onCheckedChange = { s.saveVibrate(it) })
        }
        Button(
            onClick = {
                s.saveTimings(
                    (stepText.toIntOrNull() ?: 30).coerceIn(5, 1000),
                    (homeText.toIntOrNull() ?: 500).coerceIn(50, 5000),
                    (screenText.toIntOrNull() ?: 1000).coerceIn(100, 5000)
                )
                s.showSettings = false
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.save) }
        OutlinedButton(
            onClick = { s.showSettings = false },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.back) }

        Spacer(Modifier.height(8.dp))
        Text(t.testTitle, style = MaterialTheme.typography.titleMedium)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = testCode,
                onValueChange = { testCode = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text(t.testCode) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { testCode.toIntOrNull()?.let { s.press(it) } }) { Text(t.send) }
        }
        OutlinedButton(
            onClick = { testCode.toIntOrNull()?.let { s.holdPress(it) } },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t.sendHold) }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                s.showSettings = false
                s.resetPairing()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (s.paired) t.repair else t.pairBox) }
    }
}

// ---------- gradniki daljinca ----------

/** Okrogla tipka: ob pritisku se nagne proti prstu, zasveti in se prožno vrne. */
@Composable
fun RoundKey(
    diameter: Dp,
    onDown: () -> Unit,
    onUp: () -> Unit = {},
    brush: Brush = KeyBrush,
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
            .background(brush)
            .border(1.5.dp, GlowColor.copy(alpha = 0.8f * glow.value), CircleShape)
            .pointerInput(Unit) {
                val hw = size.width / 2f
                val hh = size.height / 2f
                detectTapGestures(onPress = { o ->
                    val nx = ((o.x - hw) / hw).coerceIn(-1f, 1f)
                    val ny = ((o.y - hh) / hh).coerceIn(-1f, 1f)
                    Haptics.tick(view)
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

/** Simbol za vklop: krog z odprtino zgoraj in navpično črto. */
@Composable
fun PowerIcon(size: Dp, color: Color = Color(0xFFEF4444)) {
    Canvas(Modifier.size(size)) {
        val sw = this.size.width * 0.12f
        val pad = sw
        drawArc(
            color = color,
            startAngle = -60f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(pad, pad),
            size = Size(this.size.width - 2 * pad, this.size.height - 2 * pad),
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
        drawLine(
            color = color,
            start = Offset(center.x, pad * 0.4f),
            end = Offset(center.x, center.y),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
    }
}

/** Simbol za utišanje: zvočnik in prečrtan X. */
@Composable
fun MuteIcon(size: Dp, color: Color = TextColor) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val speaker = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.10f, h * 0.36f)
            lineTo(w * 0.28f, h * 0.36f)
            lineTo(w * 0.52f, h * 0.16f)
            lineTo(w * 0.52f, h * 0.84f)
            lineTo(w * 0.28f, h * 0.64f)
            lineTo(w * 0.10f, h * 0.64f)
            close()
        }
        drawPath(speaker, color)
        val sw = w * 0.09f
        drawLine(color, Offset(w * 0.66f, h * 0.36f), Offset(w * 0.90f, h * 0.64f), sw, StrokeCap.Round)
        drawLine(color, Offset(w * 0.90f, h * 0.36f), Offset(w * 0.66f, h * 0.64f), sw, StrokeCap.Round)
    }
}

@Composable
fun IconLabel(icon: ImageVector, label: String, k: Float, tint: Color = TextColor) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp * k), tint = tint)
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
    val t = s.tr

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
                    Haptics.tick(view)
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
        Arrow(Icons.Filled.KeyboardArrowUp, t.up, lit == 0, Modifier.align(Alignment.TopCenter).padding(top = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowDown, t.down, lit == 2, Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowLeft, t.left, lit == 3, Modifier.align(Alignment.CenterStart).padding(start = 10.dp * k), k)
        Arrow(Icons.Filled.KeyboardArrowRight, t.right, lit == 1, Modifier.align(Alignment.CenterEnd).padding(end = 10.dp * k), k)
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
    val t = s.tr
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
        if (!s.paired) {
            Text(
                t.notPaired,
                Modifier
                    .weight(1f)
                    .clickable { s.showSetup = true },
                fontSize = 12.sp,
                color = ArrowLit
            )
        } else {
            Text(
                if (s.connected) "●" else "○",
                color = if (s.connected) GreenColor else DimColor,
                fontSize = 12.sp
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (s.connected) t.connectedCh(s.channel) else s.status.ifEmpty { t.connecting },
                Modifier
                    .weight(1f)
                    .clickable { s.showChannelDialog = true },
                fontSize = 12.sp,
                color = LabelColor
            )
        }
        IconButton(onClick = { s.showSettings = true }) {
            Icon(Icons.Filled.Settings, contentDescription = t.settings, tint = DimColor)
        }
    }
}

/** Iskalno polje, prilepljeno nad telefonsko tipkovnico. */
@Composable
fun SearchDock(s: AppState, modifier: Modifier) {
    val view = LocalView.current
    val focus = remember { FocusRequester() }
    val kb = LocalSoftwareKeyboardController.current
    var focused by remember { mutableStateOf(false) }
    val t = s.tr

    fun grab() {
        try { focus.requestFocus() } catch (_: Exception) {}
        kb?.show()
        try {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        } catch (_: Exception) {}
    }

    // takoj ob odprtju: poskušaj, dokler polje ne dobi kazalca
    LaunchedEffect(Unit) {
        var tries = 0
        while (!focused && tries < 40) {
            delay(50)
            try { focus.requestFocus() } catch (_: Exception) {}
            tries++
        }
        kb?.show()
    }
    DisposableEffect(Unit) {
        onDispose { kb?.hide() }
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pa by pulse.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pa"
    )
    val borderColor = if (s.searchReady) GreenColor.copy(alpha = 0.6f) else GlowColor.copy(alpha = pa)
    val shape = RoundedCornerShape(29.dp)

    Row(
        modifier
            .fillMaxWidth()
            .height(58.dp)
            .shadow(14.dp, shape)
            .clip(shape)
            .background(Color(0xFF1A2333))
            .border(1.5.dp, borderColor, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { grab() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(26.dp), tint = GlowColor)
        }
        Column(Modifier.weight(1f)) {
            BasicTextField(
                value = s.searchText,
                onValueChange = { s.onSearchChange(it) },
                singleLine = true,
                textStyle = TextStyle(color = TextColor, fontSize = 17.sp),
                cursorBrush = SolidColor(ArrowLit),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus)
                    .onFocusChanged { focused = it.isFocused },
                decorationBox = { inner ->
                    Box {
                        if (s.searchText.isEmpty()) {
                            Text(t.searchHint, color = DimColor, fontSize = 17.sp)
                        }
                        inner()
                    }
                }
            )
            Text(
                when {
                    !focused -> t.tapToType
                    s.searchReady -> t.ready
                    else -> t.openingSearch
                },
                color = when {
                    !focused -> Color(0xFFFBBF24)
                    s.searchReady -> GreenColor
                    else -> DimColor
                },
                fontSize = 10.sp
            )
        }
        Text(
            s.kbLang.uppercase(),
            Modifier
                .border(1.dp, AccentColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 1.dp),
            color = ArrowLit,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            Modifier
                .size(58.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { s.closeSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Close, contentDescription = t.close, tint = DimColor)
        }
    }
}

/** Hiter popravek trenutnega programa. */
@Composable
fun ChannelDialog(s: AppState) {
    val t = s.tr
    var text by remember { mutableStateOf(s.channel.toString()) }
    AlertDialog(
        onDismissRequest = { s.showChannelDialog = false },
        title = { Text(t.setChTitle) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text(t.setChLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = {
                text.toIntOrNull()?.let { if (it > 0) s.setChannel(it) }
                s.showChannelDialog = false
            }) { Text(t.ok) }
        },
        dismissButton = {
            OutlinedButton(onClick = { s.showChannelDialog = false }) { Text(t.cancel) }
        }
    )
}

// ---------- daljinec ----------

@Composable
fun RemoteScreen(s: AppState) {
    val t = s.tr
    BackHandler(enabled = s.searchOpen) { s.closeSearch() }

    Box(Modifier.fillMaxSize().background(ScreenColor)) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
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
                    // vklop/izklop
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        RoundKey(48.dp * k, onDown = { s.press(26) }) {
                            PowerIcon(22.dp * k)
                        }
                    }

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

                    // nazaj, domov, išči
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            RoundKey(big, onDown = { s.press(4) }) { IconLabel(Icons.Filled.ArrowBack, t.back, k) }
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            RoundKey(big, onDown = { s.press(3) }) { IconLabel(Icons.Filled.Home, t.home, k) }
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            RoundKey(big, onDown = { s.openSearchUi() }) {
                                IconLabel(Icons.Filled.Search, t.search, k, tint = GlowColor)
                            }
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
                                MuteIcon(28.dp * k)
                            }
                        }
                        Column(
                            Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp * k)
                        ) {
                            RoundKey(small, onDown = { s.channelStep(1) }) {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = t.nextCh, modifier = Modifier.size(30.dp * k), tint = TextColor)
                            }
                            RoundKey(small, onDown = { s.channelStep(-1) }) {
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = t.prevCh, modifier = Modifier.size(30.dp * k), tint = TextColor)
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                        Text(t.volume, Modifier.weight(1f), fontSize = (11 * k).sp, color = LabelColor, textAlign = TextAlign.Center)
                        Spacer(Modifier.weight(1f))
                        Text(t.channel, Modifier.weight(1f), fontSize = (11 * k).sp, color = LabelColor, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        if (s.showChannelDialog) ChannelDialog(s)

        // iskalno polje nad tipkovnico (daljinec ostane pri miru)
        if (s.searchOpen) {
            SearchDock(
                s,
                Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}
