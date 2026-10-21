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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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

/** Droben zrnat vzorec za mat plastiko ohišja (narejen enkrat, ponavlja se). */
fun grainBrush(): Brush {
    val n = 96
    val bmp = android.graphics.Bitmap.createBitmap(n * 2, n * 2, android.graphics.Bitmap.Config.ARGB_8888)
    val rnd = java.util.Random(7)
    for (y in 0 until n) for (x in 0 until n) {
        val a = rnd.nextInt(26)
        val v = if (rnd.nextBoolean()) 255 else 0
        val c = android.graphics.Color.argb(a, v, v, v)
        // vsako "zrno" je 2 x 2 pikslov, da je vidno tudi na ostrem zaslonu
        bmp.setPixel(x * 2, y * 2, c)
        bmp.setPixel(x * 2 + 1, y * 2, c)
        bmp.setPixel(x * 2, y * 2 + 1, c)
        bmp.setPixel(x * 2 + 1, y * 2 + 1, c)
    }
    return ShaderBrush(ImageShader(bmp.asImageBitmap(), TileMode.Repeated, TileMode.Repeated))
}
val KeyBrush = Brush.verticalGradient(listOf(Color(0xFF2C3138), Color(0xFF22262C)))
val PowerBrush = Brush.verticalGradient(listOf(Color(0xFFE5484D), Color(0xFFB4232A)))

// ---------- pisava ----------

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold)
)

/** Vsi slogi besedila v Material temi s pisavo Inter. */
fun interTypography(): Typography {
    val b = Typography()
    return b.copy(
        displayLarge = b.displayLarge.copy(fontFamily = Inter),
        displayMedium = b.displayMedium.copy(fontFamily = Inter),
        displaySmall = b.displaySmall.copy(fontFamily = Inter),
        headlineLarge = b.headlineLarge.copy(fontFamily = Inter),
        headlineMedium = b.headlineMedium.copy(fontFamily = Inter),
        headlineSmall = b.headlineSmall.copy(fontFamily = Inter),
        titleLarge = b.titleLarge.copy(fontFamily = Inter),
        titleMedium = b.titleMedium.copy(fontFamily = Inter),
        titleSmall = b.titleSmall.copy(fontFamily = Inter),
        bodyLarge = b.bodyLarge.copy(fontFamily = Inter),
        bodyMedium = b.bodyMedium.copy(fontFamily = Inter),
        bodySmall = b.bodySmall.copy(fontFamily = Inter),
        labelLarge = b.labelLarge.copy(fontFamily = Inter),
        labelMedium = b.labelMedium.copy(fontFamily = Inter),
        labelSmall = b.labelSmall.copy(fontFamily = Inter)
    )
}

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
    val searching: String,
    val notListed: String,
    val enterManually: String,
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
    val showList: String,
    val drumEmpty: String,
    val drumHint: String,
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
    val connectedTo: (String) -> String,
    val pickBox: String,
    val addBox: String,
    val rename: String,
    val remove: String,
    val boxName: String,
    val boxDefault: (Int) -> String,
    val startLabel: String,
    val startAsk: String,
    val startLast: String,
    val repairThis: String,
    val searchHint: String,
    val tapToType: String,
    val ready: String,
    val openingSearch: String,
    val close: String,
    val liveTv: String,
    val connectingTo: (String) -> String,
    val notReady: String,
    val unreachable: String,
    val tipTitle: String,
    val tipNever: String,
    val tipsSetting: String,
    val lastErr: String,
    val needRepairMsg: String,
    val advanced: String,
    val welcomeTitle: String,
    val tagline: String,
    val welcomeText: String,
    val phoneLang: String,
    val next: String,
    val providerTitle: String,
    val providerText: String,
    val provTelemach: String,
    val provTelemachSub: String,
    val provOther: String,
    val provOtherSub: String,
    val start: String,
    val onlyTelemach: String,
    val vod: String,
    val searchOnTv: String,
    val boxesSec: String,
    val testsSec: String,
    val tips: List<String>,
    val ctTitle: String,
    val ctHint: String,
    val ctNext: String,
    val ctPrev: String,
    val ctAgain: String,
    val ctWorks: String,
    val ctFound: String,
    val chCount: (Int) -> String
)

val TR = mapOf(
    "sl" to Tr(
        setupTitle = "Povezava z EON boxom",
        setupInfo = "Telefon in box morata biti v istem Wi-Fi omrežju, box pa vklopljen.",
        foundBoxes = "Najdeni boxi:",
        searching = "Iščem boxe v omrežju…",
        notListed = "Boxa ni na seznamu?",
        enterManually = "Vpiši IP ročno",
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
        showList = "Pokaži prebrani seznam",
        drumEmpty = "Seznam programov se prenaša…",
        drumHint = "Program · tapni za preklop",
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
        connectedTo = { n -> "Povezano  ·  $n" },
        pickBox = "Izberi box",
        addBox = "Dodaj box",
        rename = "Preimenuj",
        remove = "Odstrani",
        boxName = "Ime boxa",
        boxDefault = { n -> "Box $n" },
        startLabel = "Ob zagonu aplikacije",
        startAsk = "Vprašaj za box",
        startLast = "Zadnji uporabljen",
        repairThis = "Ponovno upari ta box",
        searchHint = "Išči na TV",
        tapToType = "Tapni sem za tipkanje",
        ready = "Pripravljeno, tipkaj",
        openingSearch = "Odpiram iskanje na TV…",
        close = "Zapri",
        liveTv = "TV v živo",
        connectingTo = { n -> "Povezujem z $n…" },
        notReady = "Še ni povezano",
        unreachable = "Box ni dosegljiv  ·  poskušam znova",
        tipTitle = "Nasvet",
        tipNever = "Ne prikazuj več",
        tipsSetting = "Prikazuj nasvete",
        lastErr = "Zadnja napaka povezave",
        needRepairMsg = "Box ne prepozna telefona  ·  tapni za ponovno uparjanje",
        advanced = "Napredne nastavitve",
        welcomeTitle = "Dobrodošli",
        tagline = "žepni daljinec za tvoj EON box",
        welcomeText = "Upravljaj EON box s telefonom. Najprej izberi jezik.",
        phoneLang = "Jezik telefona",
        next = "Naprej",
        providerTitle = "Tvoj ponudnik",
        providerText = "Pri operaterju Telemach Slovenija daljinec prikazuje napredno funkcijo seznama programov za hitrejši preklop.",
        provTelemach = "Telemach Slovenija",
        provTelemachSub = "Seznam programov in boben",
        provOther = "Drugo",
        provOtherSub = "Tipki TV v živo in Videoklub",
        start = "Začni",
        onlyTelemach = "(samo za Telemach Slovenija)",
        vod = "Videoklub",
        searchOnTv = "Iskanje na TV",
        boxesSec = "Boxi",
        testsSec = "Preizkusi",
        tips = listOf(
            "Za pavzo in ponovno predvajanje vsebine 2× tapni tipko OK.",
            "Tapni ime boxa na vrhu za preklop med boxi.",
            "Zavrti seznam programov in tapni program za takojšen preklop.",
            "Če iskanje ne deluje pravilno, počakaj nekaj sekund pred vnosom iskalnega niza.",
            "Jezik tipkovnice v aplikaciji se mora ujemati z jezikom iskanja na boxu.",
            "Box lahko preimenuješ prek ⋮ v seznamu boxov.",
            "V Nastavitvah izbereš, ali ob zagonu vprašam za box."
        ),
        ctTitle = "Iskanje kode za program gor/dol",
        ctHint = "Na TV-ju odpri TV v živo. Tapaj Naslednja in opazuj TV. Ko se program premakne, tapni Ta deluje!",
        ctNext = "Naslednja ▶",
        ctPrev = "◀ Prejšnja",
        ctAgain = "Pošlji znova",
        ctWorks = "Ta deluje!",
        ctFound = "Delujoče kode:",
        chCount = { n ->
            val m = n % 100
            "$n " + when (m) { 1 -> "program"; 2 -> "programa"; 3, 4 -> "programi"; else -> "programov" }
        }
    ),
    "en" to Tr(
        setupTitle = "Connect to EON box",
        setupInfo = "The phone and the box must be on the same Wi-Fi network, and the box must be on.",
        foundBoxes = "Boxes found:",
        searching = "Searching for boxes on the network…",
        notListed = "Box not on the list?",
        enterManually = "Enter IP manually",
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
        showList = "Show downloaded list",
        drumEmpty = "Downloading channel list…",
        drumHint = "Channel · tap to switch",
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
        connectedTo = { n -> "Connected  ·  $n" },
        pickBox = "Choose box",
        addBox = "Add box",
        rename = "Rename",
        remove = "Remove",
        boxName = "Box name",
        boxDefault = { n -> "Box $n" },
        startLabel = "When the app starts",
        startAsk = "Ask for box",
        startLast = "Last used",
        repairThis = "Pair this box again",
        searchHint = "Search on TV",
        tapToType = "Tap here to type",
        ready = "Ready, start typing",
        openingSearch = "Opening search on TV…",
        close = "Close",
        liveTv = "Live TV",
        connectingTo = { n -> "Connecting to $n…" },
        notReady = "Not connected yet",
        unreachable = "Box not reachable  ·  retrying",
        tipTitle = "Tip",
        tipNever = "Don't show again",
        tipsSetting = "Show tips",
        lastErr = "Last connection error",
        needRepairMsg = "Box doesn't recognize this phone  ·  tap to pair again",
        advanced = "Advanced settings",
        welcomeTitle = "Welcome",
        tagline = "pocket remote for your EON box",
        welcomeText = "Control your EON box with your phone. First, choose a language.",
        phoneLang = "Phone language",
        next = "Next",
        providerTitle = "Your provider",
        providerText = "With the Telemach Slovenia operator, the remote shows an advanced channel list feature for faster switching.",
        provTelemach = "Telemach Slovenia",
        provTelemachSub = "Channel list and drum",
        provOther = "Other",
        provOtherSub = "Live TV and VOD keys",
        start = "Start",
        onlyTelemach = "(Telemach Slovenia only)",
        vod = "VOD",
        searchOnTv = "Search on TV",
        boxesSec = "Boxes",
        testsSec = "Tests",
        tips = listOf(
            "To pause and resume playback, tap OK twice.",
            "Tap the box name at the top to switch between boxes.",
            "Spin the channel list and tap a channel to switch instantly.",
            "If search doesn't work properly, wait a few seconds before typing.",
            "The keyboard language in the app must match the search language on the box.",
            "You can rename a box with ⋮ in the box list.",
            "In Settings you can choose whether to ask for a box at startup."
        ),
        ctTitle = "Find the channel up/down code",
        ctHint = "Open live TV on the box. Tap Next and watch the TV. When the channel changes, tap This works!",
        ctNext = "Next ▶",
        ctPrev = "◀ Previous",
        ctAgain = "Send again",
        ctWorks = "This works!",
        ctFound = "Working codes:",
        chCount = { n -> if (n == 1) "1 channel" else "$n channels" }
    ),
    "sr" to Tr(
        setupTitle = "Povezivanje sa EON boksom",
        setupInfo = "Telefon i boks moraju biti na istoj Wi-Fi mreži, a boks mora biti uključen.",
        foundBoxes = "Pronađeni boksovi:",
        searching = "Tražim boksove u mreži…",
        notListed = "Boksa nema na listi?",
        enterManually = "Unesi IP ručno",
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
        showList = "Prikaži preuzetu listu",
        drumEmpty = "Lista kanala se preuzima…",
        drumHint = "Kanal · dodirni za promenu",
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
        connectedTo = { n -> "Povezano  ·  $n" },
        pickBox = "Izaberi boks",
        addBox = "Dodaj boks",
        rename = "Preimenuj",
        remove = "Ukloni",
        boxName = "Ime boksa",
        boxDefault = { n -> "Boks $n" },
        startLabel = "Pri pokretanju aplikacije",
        startAsk = "Pitaj za boks",
        startLast = "Poslednji korišćen",
        repairThis = "Ponovo upari ovaj boks",
        searchHint = "Traži na TV-u",
        tapToType = "Dodirni ovde za kucanje",
        ready = "Spremno, kucaj",
        openingSearch = "Otvaram pretragu na TV-u…",
        close = "Zatvori",
        liveTv = "TV uživo",
        connectingTo = { n -> "Povezujem se sa $n…" },
        notReady = "Još nije povezano",
        unreachable = "Boks nije dostupan  ·  pokušavam ponovo",
        tipTitle = "Savet",
        tipNever = "Ne prikazuj više",
        tipsSetting = "Prikazuj savete",
        lastErr = "Poslednja greška veze",
        needRepairMsg = "Boks ne prepoznaje telefon  ·  dodirni za ponovno uparivanje",
        advanced = "Napredna podešavanja",
        welcomeTitle = "Dobro došli",
        tagline = "džepni daljinski za tvoj EON boks",
        welcomeText = "Upravljaj EON boksom preko telefona. Prvo izaberi jezik.",
        phoneLang = "Jezik telefona",
        next = "Dalje",
        providerTitle = "Tvoj provajder",
        providerText = "Kod operatera Telemach Slovenija daljinski prikazuje naprednu funkciju liste kanala za brže prebacivanje.",
        provTelemach = "Telemach Slovenija",
        provTelemachSub = "Lista kanala i bubanj",
        provOther = "Drugo",
        provOtherSub = "Tasteri TV uživo i Videoklub",
        start = "Počni",
        onlyTelemach = "(samo za Telemach Slovenija)",
        vod = "Videoklub",
        searchOnTv = "Pretraga na TV-u",
        boxesSec = "Boksovi",
        testsSec = "Testovi",
        tips = listOf(
            "Za pauzu i nastavak reprodukcije dodirni OK dva puta.",
            "Dodirni ime boksa na vrhu za prelazak između boksova.",
            "Zavrti listu kanala i dodirni kanal za trenutnu promenu.",
            "Ako pretraga ne radi kako treba, sačekaj nekoliko sekundi pre unosa.",
            "Jezik tastature u aplikaciji mora da se poklapa sa jezikom pretrage na boksu.",
            "Boks možeš da preimenuješ preko ⋮ u listi boksova.",
            "U Podešavanjima biraš da li da pitam za boks pri pokretanju."
        ),
        ctTitle = "Traženje koda za kanal gore/dole",
        ctHint = "Na TV-u otvori TV uživo. Dodiruj Sledeći i gledaj TV. Kada se kanal promeni, dodirni Ovaj radi!",
        ctNext = "Sledeći ▶",
        ctPrev = "◀ Prethodni",
        ctAgain = "Pošalji ponovo",
        ctWorks = "Ovaj radi!",
        ctFound = "Kodovi koji rade:",
        chCount = { n -> "$n kanala" }
    )
)

// ---------- vibriranje (neodvisno od nastavitve tipkovnice) ----------

object Haptics {
    var enabled = true

    /** Dva kratka sunka: "še ni povezano". */
    fun double(ctx: Context) {
        if (!enabled) return
        try {
            val vib: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                ctx.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 25, 70, 25), -1)
            if (Build.VERSION.SDK_INT >= 33) {
                vib.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(effect)
            }
        } catch (_: Exception) {}
    }

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

    /** dir: 1 = tipka pritisnjena, 2 = tipka spuščena, 3 = kratek pritisk */
    fun key(code: Int, dir: Int = 3) = send(PbWriter().msg(10, PbWriter().int(1, code).int(2, dir)))

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

// ---------- programi ----------

/** Kode, ki bi lahko preklopile program (koda, ime, ali se pošlje kot držanje). */
val CH_TEST_CODES = listOf(
    Triple(166, "CHANNEL_UP", false),
    Triple(167, "CHANNEL_DOWN", false),
    Triple(166, "CHANNEL_UP", true),
    Triple(167, "CHANNEL_DOWN", true),
    Triple(92, "PAGE_UP", false),
    Triple(93, "PAGE_DOWN", false),
    Triple(87, "MEDIA_NEXT", false),
    Triple(88, "MEDIA_PREVIOUS", false),
    Triple(272, "MEDIA_SKIP_FORWARD", false),
    Triple(273, "MEDIA_SKIP_BACKWARD", false),
    Triple(90, "MEDIA_FAST_FORWARD", false),
    Triple(89, "MEDIA_REWIND", false),
    Triple(261, "NAVIGATE_NEXT", false),
    Triple(260, "NAVIGATE_PREVIOUS", false),
    Triple(229, "LAST_CHANNEL", false),
    Triple(172, "GUIDE", false),
    Triple(165, "INFO", false),
    Triple(170, "TV", false),
    Triple(183, "PROG_RED", false),
    Triple(184, "PROG_GREEN", false),
    Triple(185, "PROG_YELLOW", false),
    Triple(186, "PROG_BLUE", false),
    Triple(19, "DPAD_UP (hold)", true),
    Triple(20, "DPAD_DOWN (hold)", true)
)

/** Od te pozicije naprej so radijski programi, teh ne beremo. */
const val RADIO_FROM = 850

/** En program iz programske sheme: pozicija, ime in naslov logotipa. */
data class Ch(val pos: Int, val name: String, val logo: String)

fun channelsToJson(list: List<Ch>): String {
    val arr = org.json.JSONArray()
    list.forEach { arr.put(org.json.JSONObject().put("p", it.pos).put("n", it.name).put("l", it.logo)) }
    return arr.toString()
}

fun channelsFromJson(t: String): List<Ch> = try {
    val arr = org.json.JSONArray(t)
    (0 until arr.length()).mapNotNull { i ->
        val o = arr.getJSONObject(i)
        val p = o.optInt("p", 0)
        if (p > 0) Ch(p, o.optString("n", "").trim(), o.optString("l", "")) else null
    }
} catch (_: Exception) {
    emptyList()
}

/** Kratica za kanal brez logotipa (npr. "POP TV" -> "POP"). */
fun initials(name: String): String {
    val up = name.filter { it.isUpperCase() || it.isDigit() }.take(3)
    return if (up.isNotEmpty()) up else name.take(2).uppercase()
}

// ---------- shranjeni boxi ----------

/** En uparjen box: ime, IP in jezik tipkovnice za iskanje na njem. */
data class BoxInfo(val id: String, val name: String, val host: String, val kbLang: String, val langDone: Boolean)

fun boxesToJson(list: List<BoxInfo>): String {
    val arr = org.json.JSONArray()
    list.forEach {
        arr.put(
            org.json.JSONObject().put("id", it.id).put("n", it.name).put("h", it.host)
                .put("k", it.kbLang).put("d", it.langDone)
        )
    }
    return arr.toString()
}

fun boxesFromJson(t: String): List<BoxInfo> = try {
    val arr = org.json.JSONArray(t)
    (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        BoxInfo(o.getString("id"), o.optString("n", "Box"), o.optString("h", ""), o.optString("k", "sl"), o.optBoolean("d", false))
    }
} catch (_: Exception) {
    emptyList()
}

// ---------- stanje aplikacije ----------

class AppState(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("daljinec", Context.MODE_PRIVATE)
    private val main = Handler(Looper.getMainLooper())
    private val net = Executors.newSingleThreadExecutor()
    private val keys = Executors.newSingleThreadExecutor()

    /** Vsi uparjeni boxi in trenutno izbrani. */
    val boxes = mutableStateListOf<BoxInfo>()
    var curId by mutableStateOf(prefs.getString("curbox", "") ?: "")
    val current: BoxInfo? get() = boxes.firstOrNull { it.id == curId }
    var showBoxPicker by mutableStateOf(false)
    /** Vodič ob prvem zagonu: končan? in na katerem koraku (0 = dobrodošlica). */
    var onboarded by mutableStateOf(true)
    var obStep by mutableStateOf(0)

    fun finishOnboarding(telemach: Boolean) {
        prefs.edit().putBoolean("chon", telemach).putBoolean("onboarded", true).apply()
        chEnabled = telemach
        onboarded = true
        if (telemach && needsSync()) syncRunner?.invoke()
    }
    var startAsk by mutableStateOf(prefs.getBoolean("startask", true))
    /** Ime boxa, ki ga je uporabnik izbral med najdenimi (za nov box). */
    var newName = ""

    var host by mutableStateOf("")
    var paired by mutableStateOf(false)
    var status by mutableStateOf("")
    var connected by mutableStateOf(false)
    var waitingCode by mutableStateOf(false)
    var busy by mutableStateOf(false)
    var channel by mutableStateOf(prefs.getInt("channel", 1))
    var channels by mutableStateOf(channelsFromJson(prefs.getString("chlist2", "[]") ?: "[]").filter { it.pos < RADIO_FROM })
    var logoTick by mutableStateOf(0)
    var showListDialog by mutableStateOf(false)
    var ctIndex by mutableStateOf(-1)
    var ctFound by mutableStateOf(prefs.getString("chcodes", "") ?: "")
    private val logoCache = HashMap<String, ImageBitmap>()
    var lastSync by mutableStateOf(prefs.getLong("chsync", 0L))
    var syncRunning by mutableStateOf(false)
    var syncFailed by mutableStateOf(false)
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
    var kbLang by mutableStateOf(systemLang())
    var uiLangPref by mutableStateOf(prefs.getString("uilang", "auto") ?: "auto")
    val tr: Tr get() = TR.getValue(if (uiLangPref == "auto") systemLang() else uiLangPref)
    var showLangSetup by mutableStateOf(false)
    val found = mutableStateListOf<Pair<String, String>>()

    init {
        Haptics.enabled = vibrate
        boxes.addAll(boxesFromJson(prefs.getString("boxes", "[]") ?: "[]"))
        // prenos iz stare verzije (en sam box)
        if (boxes.isEmpty() && prefs.getBoolean("paired", false)) {
            val h = prefs.getString("host", "") ?: ""
            if (h.isNotEmpty()) {
                boxes.add(
                    BoxInfo(
                        "b" + System.currentTimeMillis(), "Box 1", h,
                        prefs.getString("kblang", null) ?: systemLang(),
                        prefs.getBoolean("langdone", false)
                    )
                )
                curId = boxes[0].id
                saveBoxes()
            }
        }
        if (current == null && boxes.isNotEmpty()) curId = boxes[0].id
        applyCurrent()
        // ob zagonu vprašaj, kateri box (če jih je več in je tako nastavljeno)
        showBoxPicker = startAsk && boxes.size > 1
        // obstoječa namestitev (box je že uparjen): vodiča ne kažemo
        onboarded = prefs.getBoolean("onboarded", boxes.isNotEmpty())
        // enkratna zamenjava stare privzete vrednosti 80 ms s 30 ms
        if (!prefs.getBoolean("step30", false)) {
            if (prefs.getInt("step", 30) == 80) {
                prefs.edit().putInt("step", 30).apply()
                stepDelay = 30
            }
            prefs.edit().putBoolean("step30", true).apply()
        }
    }

    private fun saveBoxes() {
        prefs.edit().putString("boxes", boxesToJson(boxes)).putString("curbox", curId).apply()
    }

    /** Nastavi podatke trenutnega boxa (IP, jezik tipkovnice). */
    private fun applyCurrent() {
        val b = current
        paired = b != null
        host = b?.host ?: ""
        kbLang = b?.kbLang ?: systemLang()
        showLangSetup = b != null && !b.langDone
    }

    private fun updateCurrent(f: (BoxInfo) -> BoxInfo) {
        val i = boxes.indexOfFirst { it.id == curId }
        if (i >= 0) {
            boxes[i] = f(boxes[i])
            saveBoxes()
        }
    }

    /** Preklop na drug box. */
    fun switchBox(id: String) {
        showBoxPicker = false
        if (id == curId && connected) return
        disconnect()
        curId = id
        saveBoxes()
        closeSearch()
        applyCurrent()
        connect()
    }

    fun renameBox(id: String, name: String) {
        val i = boxes.indexOfFirst { it.id == id }
        if (i >= 0 && name.isNotBlank()) {
            boxes[i] = boxes[i].copy(name = name.trim())
            saveBoxes()
        }
    }

    fun removeBox(id: String) {
        val wasCurrent = id == curId
        boxes.removeAll { it.id == id }
        if (wasCurrent) {
            disconnect()
            curId = boxes.firstOrNull()?.id ?: ""
            applyCurrent()
            if (paired) connect()
        }
        saveBoxes()
        if (boxes.isEmpty()) {
            showBoxPicker = false
            showSetup = true
        }
    }

    /** Odpre zaslon za uparjanje novega boxa. */
    fun addBox() {
        disconnect()
        showBoxPicker = false
        showSettings = false
        host = ""
        newName = ""
        waitingCode = false
        status = ""
        showSetup = true
    }

    /** Zapre zaslon za uparjanje brez spremembe. */
    fun cancelSetup() {
        if (!onboarded) obStep = 0
        pairing?.close()
        waitingCode = false
        status = ""
        host = current?.host ?: ""
        showSetup = false
        if (paired) connect()
    }

    fun saveStartAsk(on: Boolean) {
        startAsk = on
        prefs.edit().putBoolean("startask", on).apply()
    }

    fun saveUiLang(l: String) {
        uiLangPref = l
        prefs.edit().putString("uilang", l).apply()
    }

    fun saveKbLang(l: String) {
        kbLang = l
        updateCurrent { it.copy(kbLang = l) }
    }

    /** Končano na zaslonu za jezik. */
    fun finishLangSetup() {
        updateCurrent { it.copy(langDone = true) }
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
        // med uparjanjem daljinec ne sme trkati na box (to prekine uparjanje)
        disconnect()
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
                val h = host
                main.post {
                    // isti IP že obstaja: samo ponovno uparjanje, sicer nov box
                    val old = boxes.firstOrNull { it.host == h }
                    if (old != null) {
                        curId = old.id
                    } else {
                        val name = newName.ifBlank { tr.boxDefault(boxes.size + 1) }
                        val b = BoxInfo("b" + System.currentTimeMillis(), name, h, systemLang(), false)
                        boxes.add(b)
                        curId = b.id
                    }
                    saveBoxes()
                    disconnect()
                    waitingCode = false
                    showSetup = false
                    status = ""
                    applyCurrent()
                    connect()
                }
            } catch (e: Exception) {
                status = "${tr.error}: ${e.message}"
            }
            busy = false
        }
    }

    /** Ponovno uparjanje trenutnega boxa (IP ostane vpisan). */
    fun resetPairing() {
        disconnect()
        needRepair = false
        host = current?.host ?: ""
        newName = current?.name ?: ""
        waitingCode = false
        status = ""
        showSetup = true
    }

    fun connect() {
        if (running || host.isEmpty() || showSetup || !paired) return
        running = true
        val g = ++gen
        Thread {
            while (running && g == gen) {
                val rc = RemoteConnection(ctx, host) { ok ->
                    connected = ok
                    if (ok) {
                        status = ""
                        needRepair = false
                        main.post { unreachable = false }
                        flushVolume()
                    }
                }
                remote = rc
                try {
                    rc.run()
                } catch (e: Exception) {
                    if (!showSetup) status = "${tr.noConnection}: ${e.message}"
                    if (e.message?.contains("CERTIFICATE_UNKNOWN") == true) {
                        // box tega telefona ne pozna: ne trkaj več, počakaj na ponovno uparjanje
                        needRepair = true
                        running = false
                    }
                    lastError = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date()) + "  " + (e.message ?: e.javaClass.simpleName)
                }
                if (running && g == gen) Thread.sleep(800)
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

    // ---------- dokler box ni povezan ----------

    /** Box je uparjen, a še ni povezan. */
    var unreachable by mutableStateOf(false)
    /** Box ne prepozna tega telefona (treba ga je ponovno upariti). */
    var needRepair by mutableStateOf(false)
    /** Zadnja napaka povezave (za diagnostiko v Nastavitvah). */
    var lastError by mutableStateOf("")
    var notReadyTick by mutableStateOf(0)
    private val volQueue = mutableListOf<Pair<Int, Long>>()

    /** Vrne true (in opozori), če ukaz ne more iti na box. */
    private fun blocked(): Boolean {
        if (connected || !paired) return false
        notReadyTick++
        Haptics.double(ctx)
        return true
    }

    /** Glasnost med povezovanjem: počaka v vrsti največ 4 sekunde. */
    private fun queueVolume(code: Int) {
        synchronized(volQueue) { volQueue.add(code to System.currentTimeMillis()) }
        ledTick++
    }

    private fun flushVolume() {
        val list = synchronized(volQueue) { volQueue.toList().also { volQueue.clear() } }
        val now = System.currentTimeMillis()
        val fresh = list.filter { now - it.second < 4000 }
        if (fresh.isEmpty()) return
        keys.execute {
            for ((code, _) in fresh) {
                remote?.key(code)
                Thread.sleep(80)
            }
        }
    }

    /** Če box že dolgo ni nič poslal, je povezava verjetno mrtva: začni znova. */
    fun ensureFresh() {
        val r = remote ?: return
        if (running && System.currentTimeMillis() - r.lastRx > 15000) r.close()
    }

    /** Box ni dosegljiv 4 sekunde: zavrzi čakajoče pritiske. */
    fun markUnreachable() {
        synchronized(volQueue) { volQueue.clear() }
        unreachable = true
    }

    // ---------- nasveti ----------

    var tipsOn by mutableStateOf(prefs.getBoolean("tipson", true))
    var tipShow by mutableStateOf(-1)

    /** Ali je čas za nov nasvet (največ eden na teden). */
    fun tipDue(): Int {
        if (!tipsOn || !paired) return -1
        val idx = prefs.getInt("tipidx", 0)
        if (idx >= tr.tips.size) return -1
        val last = prefs.getLong("tiplast", 0L)
        val now = System.currentTimeMillis()
        if (last == 0L) {
            // prvi zagon: prvi nasvet šele čez teden
            prefs.edit().putLong("tiplast", now).apply()
            return -1
        }
        return if (now - last > 7L * 24 * 3600 * 1000) idx else -1
    }

    fun closeTip() {
        if (tipShow < 0) return
        prefs.edit().putInt("tipidx", tipShow + 1).putLong("tiplast", System.currentTimeMillis()).apply()
        tipShow = -1
    }

    fun tipsNever() {
        tipShow = -1
        saveTipsOn(false)
    }

    fun saveTipsOn(on: Boolean) {
        tipsOn = on
        val e = prefs.edit().putBoolean("tipson", on)
        // ob ponovnem vklopu začnemo od začetka, prvi nasvet ob naslednjem odprtju
        if (on) e.putInt("tipidx", 0).putLong("tiplast", 1L)
        e.apply()
    }

    /** Kratek pritisk (konča iskanje). */
    fun press(code: Int) {
        if (blocked()) return
        closeSearch()
        ledTick++
        keys.execute { remote?.key(code, 3) }
    }

    /** Pravo držanje: tipka pritisnjena. */
    fun keyDown(code: Int) {
        if (!connected && paired) {
            if (code == 24) queueVolume(24) else blocked()
            return
        }
        closeSearch()
        ledTick++
        keys.execute { remote?.key(code, 1) }
    }

    /** Pravo držanje: tipka spuščena. */
    fun keyUp(code: Int) {
        if (!connected) return
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
    fun saveChannel(c: Int) {
        channel = c
        prefs.edit().putInt("channel", c).apply()
    }

    /** Seznam programov (boben) vklopljen; deluje samo za Telemach Slovenija. */
    var chEnabled by mutableStateOf(prefs.getBoolean("chon", true))

    fun saveChEnabled(on: Boolean) {
        chEnabled = on
        prefs.edit().putBoolean("chon", on).apply()
    }

    /** Ali je čas za dnevno osvežitev seznama programov. */
    fun needsSync(): Boolean = chEnabled && System.currentTimeMillis() - lastSync > 24L * 3600 * 1000

    /** Konec branja programske sheme (null = ni uspelo). */
    fun syncDone(list: List<Ch>?) {
        syncRunning = false
        if (list != null && list.size >= 20) {
            val sorted = list.filter { it.pos < RADIO_FROM }.distinctBy { it.pos }.sortedBy { it.pos }
            channels = sorted
            lastSync = System.currentTimeMillis()
            syncFailed = false
            prefs.edit()
                .putString("chlist2", channelsToJson(sorted))
                .putLong("chsync", lastSync)
                .apply()
            downloadLogos(sorted)
        } else {
            syncFailed = true
        }
    }

    private fun logoFile(url: String) = File(File(ctx.filesDir, "logos"), "l" + url.hashCode().toUInt())

    /** Prenese manjkajoče logotipe v telefon (v ozadju). */
    private fun downloadLogos(list: List<Ch>) {
        net.execute {
            File(ctx.filesDir, "logos").mkdirs()
            var got = 0
            for (c in list) {
                if (c.logo.isBlank() || !c.logo.startsWith("http")) continue
                val f = logoFile(c.logo)
                if (f.exists()) continue
                try {
                    val conn = java.net.URL(c.logo).openConnection() as java.net.HttpURLConnection
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000
                    conn.inputStream.use { inp -> f.outputStream().use { inp.copyTo(it) } }
                    got++
                    if (got % 20 == 0) main.post { logoTick++ }
                } catch (_: Exception) {}
            }
            main.post { logoTick++ }
        }
    }

    /** Logotip iz telefona (null, če ga še ni ali ni slika). */
    fun logoBitmap(url: String): ImageBitmap? {
        if (url.isBlank()) return null
        logoCache[url]?.let { return it }
        val f = logoFile(url)
        if (!f.exists()) return null
        val b = try { BitmapFactory.decodeFile(f.path)?.asImageBitmap() } catch (_: Exception) { null }
        if (b != null) logoCache[url] = b
        return b
    }

    fun channelName(pos: Int): String? = channels.firstOrNull { it.pos == pos }?.name?.takeIf { it.isNotBlank() }

    // glasnost −: kratek pritisk zniža, dolg pritisk (0,6 s) utiša
    private var minusMuted = false
    private val muteRunnable = Runnable {
        minusMuted = true
        if (!connected && paired) {
            queueVolume(164)
        } else {
            ledTick++
            keys.execute { remote?.key(164) }
        }
    }

    fun volMinusDown() {
        closeSearch()
        minusMuted = false
        main.postDelayed(muteRunnable, 600)
    }

    fun volMinusUp() {
        main.removeCallbacks(muteRunnable)
        if (!minusMuted) {
            if (!connected && paired) queueVolume(25) else press(25)
        }
    }

    /** Številka s številčnice: pošlje tipko in si zapomni program. */
    fun digit(n: Int) {
        if (blocked()) return
        val now = System.currentTimeMillis()
        entry = if (now - lastDigit < 2000 && entry.length < 4) entry + n else n.toString()
        lastDigit = now
        entry.toIntOrNull()?.let { if (it > 0) saveChannel(it) }
        press(7 + n)
    }

    /** Test kod: pošlje kodo na mestu i (kratek pritisk ali držanje). */
    fun ctSend(i: Int) {
        ctIndex = i
        val (code, _, hold) = CH_TEST_CODES[i]
        if (hold) holdPress(code) else press(code)
    }

    /** Test kod: zapomni si trenutno kodo kot delujočo. */
    fun ctMarkWorks() {
        if (ctIndex < 0) return
        val (code, name, hold) = CH_TEST_CODES[ctIndex]
        val item = "$code $name" + if (hold) " ⏷" else ""
        val list = ctFound.split(", ").filter { it.isNotBlank() }
        if (item in list) return
        ctFound = (list + item).joinToString(", ")
        prefs.edit().putString("chcodes", ctFound).apply()
    }

    /** Tipka Videoklub: Domov, desno, desno, OK. */
    fun openVod() {
        if (blocked()) return
        closeSearch()
        ledTick++
        keys.execute {
            kbOpen = false
            step(3, homeWait.toLong())
            step(22, 250)
            step(22, 600)   // počakaj, da se izbira umiri, sicer box OK prezre
            step(23)
            kbOpen = false
        }
    }

    /** Tipka TV v živo: Domov, desno, dol, OK (odpre zadnji gledani program). */
    fun openLiveTv() {
        if (blocked()) return
        closeSearch()
        ledTick++
        keys.execute {
            kbOpen = false
            step(3, homeWait.toLong())
            step(22, 250)
            step(20, 600)   // počakaj, da se izbira umiri, sicer box OK prezre
            step(23)
            kbOpen = false
        }
    }

    /** Tap na program v bobnu: vtipka številko in potrdi z OK. */
    fun pickChannel(c: Int) {
        if (blocked()) return
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
        }
    }

    // ---------- iskanje na EON tipkovnici ----------

    /** Tap na lupo: razširi polje in takoj začne odpirati iskanje na TV-ju. */
    fun openSearchUi() {
        if (searchOpen) return
        if (blocked()) return
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
                ),
                typography = interTypography()
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
        // ob zaprtju takoj lepo zapremo povezavo, da box sprosti mesto
        state.disconnect()
    }

    override fun onDestroy() {
        stopSync(null)
        super.onDestroy()
    }

    // ---------- branje programske sheme v ozadju (nevidni brskalnik) ----------

    private var web: WebView? = null
    private val syncHandler = Handler(Looper.getMainLooper())
    private var best: List<Ch> = emptyList()

    private fun stopSync(result: List<Ch>?) {
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
        // nevidni brskalnik čez cel zaslon (da se stran normalno izriše),
        // dotike pa spusti naprej do daljinca
        val w = object : WebView(this) {
            override fun dispatchTouchEvent(event: android.view.MotionEvent?): Boolean = false
        }
        web = w
        w.alpha = 0f
        w.isFocusable = false
        addContentView(w, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        w.settings.javaScriptEnabled = true
        w.settings.domStorageEnabled = true

        val acc = LinkedHashMap<Int, Ch>()
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
                    val list = if (arr != null) channelsFromJson(arr.toString()) else emptyList()
                    // združi z že prebranimi (če stran nalaga po delih)
                    for (c in list) {
                        val old = acc[c.pos]
                        acc[c.pos] = if (old == null) c else Ch(
                            c.pos,
                            old.name.ifBlank { c.name },
                            old.logo.ifBlank { c.logo }
                        )
                    }
                    best = acc.values.toList()
                } catch (_: Exception) {}
                if (!more && best.size == lastCount) stable++ else stable = 0
                lastCount = best.size
                if ((stable >= 3 && (best.isNotEmpty() || iter >= 10)) || iter >= 80) stopSync(best) else syncHandler.postDelayed(tick, 2000)
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
        // varovalo: najdlje 3 minute
        syncHandler.postDelayed({ if (web === w) stopSync(best) }, 180_000)
        w.loadUrl("https://sl.eon.tv/programska-shema/")
    }
}

/** Na strani klikne "Prikaži več" (če obstaja) in prebere pozicijo, ime in logotip vseh programov. */
private const val SYNC_JS = """
(function(){
  function abs(u){ try { return new URL(u, location.href).href; } catch(e) { return u || ''; } }
  function imgOf(el){
    if (!el) return '';
    var im = el.querySelector('img');
    if (im) return abs(im.currentSrc || im.src || im.getAttribute('data-src') || im.getAttribute('data-lazy-src') || '');
    var all = el.querySelectorAll('*');
    for (var i = 0; i < all.length; i++) {
      var bg = getComputedStyle(all[i]).backgroundImage;
      if (bg && bg.indexOf('url(') == 0) return abs(bg.slice(4, -1).replace(/["']/g, ''));
    }
    return '';
  }
  function num(t){ var m = (t || '').match(/\d{1,4}/); return m ? parseInt(m[0]) : 0; }
  var out = {};
  function put(p, n, l){
    if (!(p > 0)) return;
    var o = out[p] || { p: p, n: '', l: '' };
    if (n && !o.n) o.n = n.replace(/\s+/g, ' ').trim();
    if (l && !o.l) o.l = l;
    out[p] = o;
  }
  // gumb "Prikaži več": vzamemo najbolj notranji element s tem besedilom
  // in kliknemo njegov pravi gumb (button/a), ne ovojnice okoli njega
  var re = /^(prika[zž]i|poka[zž]i|nalo[zž]i)\s+(ve[cč]|vse)|^(show|load)\s+more/;
  var hits = [];
  var els = document.querySelectorAll('button,a,span,div,p,li,[role=button]');
  for (var i = 0; i < els.length; i++) {
    var tx = (els[i].textContent || '').replace(/\s+/g, ' ').trim().toLowerCase();
    if (tx.length < 40 && re.test(tx)) hits.push(els[i]);
  }
  var btn = null, spare = null;
  for (var i = 0; i < hits.length; i++) {
    var inner = true;
    for (var j = 0; j < hits.length; j++) if (j != i && hits[i].contains(hits[j])) { inner = false; break; }
    if (!inner) continue;
    var tg = hits[i].closest('button,a,[role=button]') || hits[i];
    if (tg.disabled) continue;
    if (tg.getClientRects().length > 0) { btn = tg; break; }
    if (!spare) spare = tg;
  }
  if (!btn) btn = spare;
  window.scrollTo(0, document.body.scrollHeight);
  var tables = document.querySelectorAll('table');
  for (var b = 0; b < tables.length; b++) {
    var hs = tables[b].querySelectorAll('th'), ip = -1, ik = -1, il = -1;
    for (var j = 0; j < hs.length; j++) {
      var h = hs[j].textContent || '';
      if (/pozicija/i.test(h)) ip = j;
      else if (/kanal/i.test(h)) ik = j;
      else if (/logo/i.test(h)) il = j;
    }
    if (ip < 0) continue;
    var rows = tables[b].querySelectorAll('tr');
    for (var r = 0; r < rows.length; r++) {
      var td = rows[r].querySelectorAll('td');
      if (td.length <= ip) continue;
      put(num(td[ip].textContent), ik >= 0 && td[ik] ? td[ik].textContent : '', il >= 0 && td[il] ? imgOf(td[il]) : imgOf(rows[r]));
    }
  }
  var cand = document.body.querySelectorAll('div,li,article,section,tr');
  for (var c = 0; c < cand.length; c++) {
    var el = cand[c];
    var tc = el.textContent || '';
    if (tc.length > 600 || !/Pozicija/i.test(tc) || !/Kanal/i.test(tc)) continue;
    var minimal = true;
    for (var k = 0; k < el.children.length; k++) {
      var ct = el.children[k].textContent || '';
      if (/Pozicija/i.test(ct) && /Kanal/i.test(ct)) { minimal = false; break; }
    }
    if (!minimal) continue;
    var t = el.innerText || tc;
    var mp = t.match(/Pozicija\s*:?\s*(\d{1,4})/i);
    var mk = t.match(/Kanal\s*:?\s*\n?\s*([^\n]+)/i);
    put(mp ? parseInt(mp[1]) : 0, mk ? mk[1] : '', imgOf(el));
  }
  var list = Object.keys(out).map(function(k){ return out[k]; });
  if (btn) {
    try { btn.scrollIntoView({ block: 'center' }); } catch (e) {}
    var ev = ['pointerdown', 'mousedown', 'pointerup', 'mouseup', 'click'];
    for (var i = 0; i < ev.length; i++) {
      try { btn.dispatchEvent(new MouseEvent(ev[i], { bubbles: true, cancelable: true, view: window })); } catch (e) {}
    }
  }
  return JSON.stringify({ more: !!btn, list: list });
})()
"""

@Composable
fun App(s: AppState) {
    when {
        !s.onboarded && s.obStep == 0 -> WelcomeScreen(s)
        s.showSetup || (!s.onboarded && !s.paired) -> SetupScreen(s)
        s.showLangSetup -> LangScreen(s)
        !s.onboarded -> ProviderScreen(s)
        s.showSettings -> SettingsScreen(s)
        else -> RemoteScreen(s)
    }
    if (s.showBoxPicker && !s.showSetup) BoxPickerDialog(s)
}

// ---------- vodič ob prvem zagonu ----------

/** Ime aplikacije (enako v vseh jezikih). */
const val APP_NAME = "Pocket Remote"

/** Črtice napredka na vrhu (korak od 1 do n). */
@Composable
fun StepBar(step: Int, total: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i < step) AccentColor else Color(0xFF23272E))
            )
        }
    }
}

/** Izbira v obliki kartice z okroglim izbirnikom. */
@Composable
fun OptionRow(title: String, sub: String?, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) Color(0xFF14213A) else Color(0xFF111418))
            .border(if (selected) 1.5.dp else 1.dp, if (selected) AccentColor else Color(0xFF23272E), shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(if (selected) 5.dp else 1.5.dp, if (selected) AccentColor else Color(0xFF4B5563), CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 15.sp, color = TextColor)
            if (sub != null) Text(sub, fontSize = 12.sp, color = Color(0xFF6B7280))
        }
    }
}

/** Ikona aplikacije: daljinec na modrem. */
@Composable
fun AppLogo(size: Dp) {
    Canvas(
        Modifier
            .size(size)
            .shadow(20.dp, RoundedCornerShape(size * 0.28f), ambientColor = AccentColor, spotColor = AccentColor)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(Color(0xFF2F6FE4))
    ) {
        val u = this.size.width / 108f
        drawRoundRect(Color(0xFF15181D), Offset(40 * u, 22 * u), Size(28 * u, 64 * u), androidx.compose.ui.geometry.CornerRadius(14 * u))
        drawCircle(Color(0xFFEF4444), 4.2f * u, Offset(54 * u, 33 * u), style = Stroke(2.2f * u))
        drawCircle(Color(0xFF60A5FA), 9 * u, Offset(54 * u, 50 * u))
        for (y in listOf(66f, 74f)) for (x in listOf(47.5f, 54f, 60.5f)) {
            drawCircle(Color(0xFF8A9099), 2.4f * u, Offset(x * u, y * u))
        }
    }
}

@Composable
fun WelcomeScreen(s: AppState) {
    val t = s.tr
    val sys = systemLang()
    val cur = if (s.uiLangPref == "auto") sys else s.uiLangPref
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StepBar(1, 4)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { AppLogo(80.dp) }
        Text(APP_NAME, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ArrowLit,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Text(t.tagline, fontSize = 12.sp, color = DimColor,
            modifier = Modifier.fillMaxWidth().padding(top = 0.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(t.welcomeTitle, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextColor,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Text(t.welcomeText, fontSize = 14.sp, color = DimColor,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        LANG_NAMES.forEach { (code, name) ->
            OptionRow(name, if (code == sys) t.phoneLang else null, cur == code) {
                s.saveUiLang(if (code == sys) "auto" else code)
            }
        }
        Spacer(Modifier.height(12.dp))
        GlowButton(t.next, enabled = true) { s.obStep = 1 }
    }
}

@Composable
fun ProviderScreen(s: AppState) {
    val t = s.tr
    var telemach by remember { mutableStateOf(true) }
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StepBar(4, 4)
        Spacer(Modifier.height(12.dp))
        Text(t.providerTitle, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextColor)
        Text(t.providerText, fontSize = 14.sp, color = DimColor)
        Spacer(Modifier.height(8.dp))
        OptionRow(t.provTelemach, t.provTelemachSub, telemach) { telemach = true }
        OptionRow(t.provOther, t.provOtherSub, !telemach) { telemach = false }
        Spacer(Modifier.height(12.dp))
        GlowButton(t.start, enabled = true) { s.finishOnboarding(telemach) }
    }
}

// ---------- uparjanje ----------

@Composable
fun SetupScreen(s: AppState) {
    val ctx = LocalContext.current
    val t = s.tr
    var code by remember { mutableStateOf("") }
    var manual by remember { mutableStateOf(false) }
    val codeFocus = remember { FocusRequester() }

    BackHandler { s.cancelSetup() }

    DisposableEffect(Unit) {
        val d = Discovery(ctx) { name, ip -> s.addFound(name, ip) }
        d.start()
        onDispose { d.stop() }
    }
    LaunchedEffect(s.waitingCode) {
        if (s.waitingCode) {
            code = ""
            delay(250)
            try { codeFocus.requestFocus() } catch (_: Exception) {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // korak 1 / korak 2
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(true, s.waitingCode).forEach { on ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (on) AccentColor else Color(0xFF23272E))
                )
            }
        }
        Text(t.setupTitle, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = TextColor)
        Text(t.setupInfo, fontSize = 13.sp, color = DimColor)

        if (!s.waitingCode) {
            // iskanje v omrežju
            Row(verticalAlignment = Alignment.CenterVertically) {
                Radar(18.dp)
                Spacer(Modifier.width(10.dp))
                Text(t.searching, fontSize = 13.sp, color = ArrowLit)
            }
        }

        s.found.forEach { (name, ip) ->
            val sel = s.host == ip && (s.busy || s.waitingCode)
            Row(
                Modifier
                    .fillMaxWidth()
                    .shadow(if (sel) 10.dp else 0.dp, RoundedCornerShape(16.dp), ambientColor = AccentColor, spotColor = AccentColor)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (sel) Color(0xFF14213A) else Color(0xFF111418))
                    .border(if (sel) 1.5.dp else 1.dp, if (sel) AccentColor else Color(0xFF23272E), RoundedCornerShape(16.dp))
                    .clickable(enabled = !s.busy) {
                        s.host = ip
                        s.newName = name
                        s.startPairing()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B2230)),
                    contentAlignment = Alignment.Center
                ) { TvIcon(22.dp) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, fontSize = 15.sp, color = TextColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(ip, fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF4B5563))
            }
        }

        if (!s.waitingCode) {
            // ročni vnos IP
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { manual = !manual }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(t.notListed + "  ", fontSize = 13.sp, color = DimColor)
                Text(t.enterManually, fontSize = 13.sp, color = ArrowLit)
            }
            if (manual) {
                OutlinedTextField(
                    value = s.host,
                    onValueChange = { s.host = it.trim() },
                    label = { Text(t.ipLabel) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )
                GlowButton(t.pair, enabled = s.host.isNotEmpty() && !s.busy) {
                    s.newName = ""
                    s.startPairing()
                }
            }
        }

        if (s.waitingCode) {
            Text(t.enterCode, fontSize = 14.sp, color = Color(0xFFCBD5E1), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            // šest okenc za kodo s TV-ja
            BasicTextField(
                value = code,
                onValueChange = { v ->
                    code = v.uppercase().filter { it in "0123456789ABCDEF" }.take(6)
                    // ko je vpisanih vseh 6 znakov, se povezovanje začne samo
                    if (code.length == 6 && !s.busy) s.finishPairing(code)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier.fillMaxWidth().focusRequester(codeFocus),
                decorationBox = { _ ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                        repeat(6) { i ->
                            val cur = i == code.length
                            Box(
                                Modifier
                                    .size(42.dp, 52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF111418))
                                    .border(if (cur) 1.5.dp else 1.dp, if (cur) AccentColor else Color(0xFF2A3038), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(code.getOrNull(i)?.toString() ?: "", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = TextColor)
                            }
                        }
                    }
                }
            )
        }

        if (s.status.isNotEmpty() && s.status != t.enterCode) {
            Text(s.status, fontSize = 13.sp, color = Color(0xFFFBBF24))
        }
        Text(
            t.back,
            Modifier
                .fillMaxWidth()
                .clickable { s.cancelSetup() }
                .padding(vertical = 10.dp),
            color = DimColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

/** Velik moder gumb s sijem. */
@Composable
fun GlowButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(if (enabled) 12.dp else 0.dp, shape, ambientColor = AccentColor, spotColor = AccentColor)
            .clip(shape)
            .background(
                if (enabled) Brush.verticalGradient(listOf(Color(0xFF4B8DFF), Color(0xFF2F6FE4)))
                else SolidColor(Color(0xFF23272E))
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (enabled) Color.White else DimColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

/** Utripajoči valovi med iskanjem (kot radar). */
@Composable
fun Radar(size: Dp) {
    val tr = rememberInfiniteTransition(label = "radar")
    val ph by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "ph"
    )
    Canvas(Modifier.size(size)) {
        for (j in 0 until 3) {
            val p = (ph + j / 3f) % 1f
            drawCircle(
                GlowColor.copy(alpha = 1f - p),
                radius = this.size.minDimension / 2 * (0.2f + 1.2f * p),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
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
    var advanced by remember { mutableStateOf(false) }
    var stepText by remember { mutableStateOf(s.stepDelay.toString()) }
    var homeText by remember { mutableStateOf(s.homeWait.toString()) }
    var screenText by remember { mutableStateOf(s.screenWait.toString()) }
    var testCode by remember { mutableStateOf("") }
    val t = s.tr

    fun saveTimes() {
        s.saveTimings(
            (stepText.toIntOrNull() ?: 30).coerceIn(5, 1000),
            (homeText.toIntOrNull() ?: 500).coerceIn(50, 5000),
            (screenText.toIntOrNull() ?: 1000).coerceIn(100, 5000)
        )
    }
    fun leave() {
        if (advanced) {
            saveTimes()
            advanced = false
        } else {
            s.showSettings = false
        }
    }

    BackHandler { leave() }
    // shrani čase tudi, če se nastavitve zaprejo kako drugače
    DisposableEffect(Unit) { onDispose { saveTimes() } }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { leave() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = t.back, tint = LabelColor)
            }
            Text(if (advanced) t.advanced else t.settings, style = MaterialTheme.typography.titleLarge)
        }

        if (!advanced) {
            // ---------- prva stopnja ----------
            Text(t.appLang, style = MaterialTheme.typography.titleMedium)
            Choices(listOf("auto" to t.auto) + LANG_NAMES, s.uiLangPref, 2) { s.saveUiLang(it) }

            Text(t.kbTitle, style = MaterialTheme.typography.titleMedium)
            LangPicker(s)
            Text(t.kbMustMatch, color = DimColor, fontSize = 13.sp)
            OutlinedButton(
                onClick = { s.showLangSetup = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text(t.kbSetOnBox) }

            Text(t.startLabel, style = MaterialTheme.typography.titleMedium)
            Choices(listOf("ask" to t.startAsk, "last" to t.startLast), if (s.startAsk) "ask" else "last", 2) {
                s.saveStartAsk(it == "ask")
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(t.vibrate, Modifier.weight(1f))
                Switch(checked = s.vibrate, onCheckedChange = { s.saveVibrate(it) })
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(t.tipsSetting, Modifier.weight(1f))
                Switch(checked = s.tipsOn, onCheckedChange = { s.saveTipsOn(it) })
            }

            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick = { advanced = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(t.advanced, color = TextColor, modifier = Modifier.weight(1f))
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = DimColor)
            }
        } else {
            // ---------- napredne nastavitve ----------
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(t.chTitle, style = MaterialTheme.typography.titleMedium)
                    Text(t.onlyTelemach, color = DimColor, fontSize = 12.sp)
                }
                Switch(checked = s.chEnabled, onCheckedChange = { s.saveChEnabled(it) })
            }
            if (s.chEnabled) {
                Text(
                    when {
                        s.syncRunning -> t.syncRunning
                        s.lastSync == 0L -> t.syncNever
                        else -> t.syncLast(
                            java.text.SimpleDateFormat("d. M. yyyy HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(s.lastSync)),
                            s.channels.size
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
                OutlinedButton(
                    onClick = { s.showListDialog = true },
                    enabled = s.channels.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(t.showList) }
                if (s.showListDialog) ListDialog(s)
            }

            Spacer(Modifier.height(4.dp))
            Text(t.searchOnTv, style = MaterialTheme.typography.titleMedium)
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

            Spacer(Modifier.height(4.dp))
            Text(t.boxesSec, style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = { s.addBox() },
                modifier = Modifier.fillMaxWidth()
            ) { Text(t.addBox) }
            if (s.paired) {
                OutlinedButton(
                    onClick = {
                        s.showSettings = false
                        s.resetPairing()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(t.repairThis) }
            }

            Spacer(Modifier.height(4.dp))
            Text(t.testsSec, style = MaterialTheme.typography.titleMedium)
            Text(t.testTitle, color = LabelColor, fontSize = 14.sp)
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

            Spacer(Modifier.height(4.dp))
            Text(t.ctTitle, color = LabelColor, fontSize = 14.sp)
            Text(t.ctHint, color = DimColor, fontSize = 13.sp)
            val ci = s.ctIndex
            Text(
                if (ci < 0) "— / ${CH_TEST_CODES.size}"
                else CH_TEST_CODES[ci].let { (code, name, hold) ->
                    "${ci + 1} / ${CH_TEST_CODES.size}  ·  $code  ·  $name" + if (hold) "  ·  ⏷" else ""
                },
                color = TextColor, fontSize = 15.sp, fontWeight = FontWeight.Medium
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { if (ci > 0) s.ctSend(ci - 1) },
                    enabled = ci > 0,
                    modifier = Modifier.weight(1f)
                ) { Text(t.ctPrev) }
                Button(
                    onClick = { if (ci < CH_TEST_CODES.size - 1) s.ctSend(ci + 1) },
                    enabled = ci < CH_TEST_CODES.size - 1,
                    modifier = Modifier.weight(1f)
                ) { Text(t.ctNext) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { if (ci >= 0) s.ctSend(ci) },
                    enabled = ci >= 0,
                    modifier = Modifier.weight(1f)
                ) { Text(t.ctAgain) }
                OutlinedButton(
                    onClick = { s.press(4) },
                    modifier = Modifier.weight(1f)
                ) { Text(t.back) }
            }
            Button(
                onClick = { s.ctMarkWorks() },
                enabled = ci >= 0,
                modifier = Modifier.fillMaxWidth()
            ) { Text(t.ctWorks) }
            if (s.ctFound.isNotBlank()) Text("${t.ctFound} ${s.ctFound}", color = GreenColor, fontSize = 13.sp)

            if (s.lastError.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("${t.lastErr}: ${s.lastError}", color = DimColor, fontSize = 12.sp)
            }
        }
    }
}

// ---------- gradniki daljinca ----------

/** Jakost svetlobnega pulza glede na napredek 0..1: hiter vzpon, počasen upad. */
fun pulseAlpha(p: Float): Float = when {
    p <= 0f || p >= 1f -> 0f
    p < 0.25f -> p / 0.25f
    else -> 1f - (p - 0.25f) / 0.75f
}

/** Svetel madež (moder sij) okoli središča c, za tipko velikosti w x h. */
fun DrawScope.glowBlob(c: Offset, w: Float, h: Float, p: Float, spread: Float) {
    val a = pulseAlpha(p)
    if (a <= 0f || h <= 0f) return
    val grow = 0.85f + 0.3f * p
    val r = h / 2 * spread * grow
    scale(scaleX = w / h, scaleY = 1f, pivot = c) {
        drawCircle(
            Brush.radialGradient(
                0f to Color(0xFF6EAAFF).copy(alpha = 0.95f * a),
                0.5f to AccentColor.copy(alpha = 0.6f * a),
                1f to Color.Transparent,
                center = c,
                radius = r
            ),
            radius = r,
            center = c
        )
    }
}

/** Trajanje svetlobnega pulza pod tipko. */
const val PULSE_NS = 360_000_000L

/** Skupna plast za svetlobo pod tipkami: riše se na ohišju, pod vsemi tipkami,
 *  zato svetloba gre pod sosednje tipke in jih ne prekrije. */
class GlowLayer {
    class Item(val rect: Rect, val dx: Float, val dy: Float, val spread: Float, val start: Long)
    val items = mutableStateListOf<Item>()
    var origin = Offset.Zero
    var now by mutableStateOf(0L)

    fun fire(rect: Rect, spread: Float, dx: Float = 0f, dy: Float = 0f) {
        if (rect.width <= 0f) return
        items.add(Item(rect, dx, dy, spread, System.nanoTime()))
    }

    fun draw(scope: DrawScope) {
        val n = now
        for (it in items) {
            val p = ((n - it.start).toFloat() / PULSE_NS).coerceIn(0f, 1f)
            val c = Offset(it.rect.center.x - origin.x + it.dx, it.rect.center.y - origin.y + it.dy)
            scope.glowBlob(c, it.rect.width, it.rect.height, p, it.spread)
        }
    }
}

val LocalGlow = staticCompositionLocalOf<GlowLayer?> { null }

/** Okrogla tipka: ob pritisku se pogrezne v ohišje, pod njo posveti, nato se počasi vrne. */
@Composable
fun RoundKey(
    diameter: Dp,
    onDown: () -> Unit,
    onUp: () -> Unit = {},
    brush: Brush = KeyBrush,
    width: Dp = diameter,
    content: @Composable BoxScope.() -> Unit
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val glowLayer = LocalGlow.current
    val sink = remember { Animatable(0f) }
    val bounds = remember { arrayOf(Rect.Zero) }
    val down by rememberUpdatedState(onDown)
    val up by rememberUpdatedState(onUp)

    Box(
        Modifier
            .size(width, diameter)
            .onGloballyPositioned { bounds[0] = it.boundsInRoot() }
            .graphicsLayer {
                val s = sink.value
                scaleX = 1f - 0.06f * s
                scaleY = 1f - 0.06f * s
                translationY = 1.dp.toPx() * s.coerceAtLeast(0f)
                shadowElevation = 6.dp.toPx() * (1f - s.coerceIn(0f, 1f))
                shape = CircleShape
                clip = true
            }
            .background(brush)
            // pogreznjena tipka: zgoraj temnejša, z notranjo senco
            .drawBehind {
                val s = sink.value.coerceIn(0f, 1f)
                if (s > 0f) {
                    drawRect(Brush.verticalGradient(listOf(Color(0xFF1B1E24), Color(0xFF272B32))), alpha = s)
                    drawRect(
                        Brush.verticalGradient(0f to Color.Black.copy(alpha = 0.6f), 0.4f to Color.Transparent),
                        alpha = s
                    )
                }
            }
            .drawWithContent {
                drawContent()
                val s = sink.value.coerceIn(0f, 1f)
                if (s > 0f) drawRect(Color.Black.copy(alpha = 0.15f * s))
            }
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    val t0 = System.currentTimeMillis()
                    Haptics.tick(view)
                    down()
                    glowLayer?.fire(bounds[0], 1.75f)
                    scope.launch { sink.animateTo(1f, tween(70)) }
                    tryAwaitRelease()
                    up()
                    scope.launch {
                        val held = System.currentTimeMillis() - t0
                        if (held < 110) delay(110 - held)
                        // počasen povratek z rahlim odbojem
                        sink.animateTo(-0.2f, tween(520, easing = FastOutSlowInEasing))
                        sink.animateTo(0f, tween(230, easing = FastOutSlowInEasing))
                    }
                })
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

/** Simbol za televizor: zaslon z nogico. */
@Composable
fun TvIcon(size: Dp, color: Color = GlowColor) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.09f
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.08f, h * 0.14f),
            size = Size(w * 0.84f, h * 0.58f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.1f),
            style = Stroke(width = sw)
        )
        drawLine(color, Offset(w * 0.34f, h * 0.88f), Offset(w * 0.66f, h * 0.88f), sw, StrokeCap.Round)
    }
}

/** Simbol za videoklub: filmski trak. */
@Composable
fun FilmIcon(size: Dp, color: Color = GlowColor) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.09f
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.12f, h * 0.16f),
            size = Size(w * 0.76f, h * 0.68f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f),
            style = Stroke(width = sw)
        )
        drawLine(color, Offset(w * 0.30f, h * 0.16f), Offset(w * 0.30f, h * 0.84f), sw * 0.8f)
        drawLine(color, Offset(w * 0.70f, h * 0.16f), Offset(w * 0.70f, h * 0.84f), sw * 0.8f)
        drawLine(color, Offset(w * 0.30f, h * 0.5f), Offset(w * 0.70f, h * 0.5f), sw * 0.8f)
    }
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
    val glowLayer = LocalGlow.current
    val rx = remember { Animatable(0f) }
    val ry = remember { Animatable(0f) }
    val dsc = remember { Animatable(1f) }
    val okScale = remember { Animatable(1f) }
    val okFace = remember { Animatable(1f) }
    val blink = remember { Animatable(0f) }
    val bounds = remember { arrayOf(Rect.Zero) }
    var lit by remember { mutableStateOf(-1) } // 0 gor, 1 desno, 2 dol, 3 levo
    val t = s.tr
    val conn by animateFloatAsState(if (s.connected) 1f else 0f, tween(500), label = "conn")
    // vrteč se krog okoli OK, dokler se box ne poveže
    val spin = remember { Animatable(0f) }
    LaunchedEffect(s.connected) {
        while (!s.connected) {
            spin.snapTo(0f)
            spin.animateTo(360f, tween(1100, easing = LinearEasing))
        }
    }
    // OK vsakih nekaj sekund komaj opazno utripne (samo ko je box povezan)
    LaunchedEffect(s.connected) {
        while (s.connected) {
            delay(5000)
            blink.animateTo(1f, tween(440, easing = FastOutSlowInEasing))
            blink.animateTo(0f, tween(660, easing = FastOutSlowInEasing))
        }
        blink.snapTo(0f)
    }

    /** Počasen povratek z rahlim odbojem (enako kot pri tipkah). */
    suspend fun Animatable<Float, androidx.compose.animation.core.AnimationVector1D>.release(rest: Float, from: Float) {
        animateTo(rest + (rest - from) * 0.2f, tween(520, easing = FastOutSlowInEasing))
        animateTo(rest, tween(230, easing = FastOutSlowInEasing))
    }

    Box(
        Modifier
            .size(diameter)
            .onGloballyPositioned { bounds[0] = it.boundsInRoot() }
    ) {
    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = rx.value
                rotationY = ry.value
                scaleX = dsc.value
                scaleY = dsc.value
                cameraDistance = 14f * density
                shadowElevation = 10.dp.toPx() * (0.4f + 0.6f * ((dsc.value - 0.98f) / 0.02f).coerceIn(0f, 1f))
                shape = CircleShape
                clip = true
            }
            .background(Brush.radialGradient(listOf(Color(0xFF2E333A), Color(0xFF1F2328))))
            // OK ob pritisku: nežen sij čez površino kroga in puščice
            .drawWithContent {
                drawContent()
                val a = pulseAlpha(okFace.value)
                if (a > 0f) {
                    drawCircle(
                        Brush.radialGradient(
                            0f to GlowColor.copy(alpha = 0.38f * a),
                            0.55f to GlowColor.copy(alpha = 0.12f * a),
                            0.75f to Color.Transparent
                        )
                    )
                }
            }
            .pointerInput(Unit) {
                val c = size.width / 2f
                detectTapGestures(onPress = { o ->
                    val dx = o.x - c
                    val dy = o.y - c
                    Haptics.tick(view)
                    val t0 = System.currentTimeMillis()
                    if (hypot(dx, dy) < c * 0.37f) {
                        s.press(23)
                        scope.launch {
                            okFace.snapTo(0f)
                            okFace.animateTo(1f, tween(420, easing = LinearEasing))
                        }
                        scope.launch { okScale.animateTo(0.93f, tween(70)) }
                        tryAwaitRelease()
                        scope.launch {
                            val held = System.currentTimeMillis() - t0
                            if (held < 110) delay(110 - held)
                            okScale.release(1f, 0.93f)
                        }
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
                        val r = bounds[0]
                        val off = r.width * 0.19f
                        glowLayer?.fire(
                            r, 1.1f,
                            floatArrayOf(0f, 1f, 0f, -1f)[dir] * off,
                            floatArrayOf(-1f, 0f, 1f, 0f)[dir] * off
                        )
                        s.keyDown(code)
                        val tx = floatArrayOf(10f, 0f, -10f, 0f)[dir]
                        val ty = floatArrayOf(0f, 10f, 0f, -10f)[dir]
                        scope.launch { rx.animateTo(tx, tween(70)) }
                        scope.launch { ry.animateTo(ty, tween(70)) }
                        scope.launch { dsc.animateTo(0.98f, tween(70)) }
                        tryAwaitRelease()
                        s.keyUp(code)
                        scope.launch {
                            val held = System.currentTimeMillis() - t0
                            if (held < 110) delay(110 - held)
                            launch { rx.release(0f, tx) }
                            launch { ry.release(0f, ty) }
                            dsc.release(1f, 0.98f)
                        }
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
        // dihajoč sij okoli OK (samo ko je box povezan) in vrteč se krog med povezovanjem
        Box(
            Modifier
                .align(Alignment.Center)
                .size(diameter * 0.62f)
                .drawBehind {
                    val ra = 1f - conn
                    if (ra > 0.01f && s.paired) {
                        val ringR = diameter.toPx() * 0.37f / 2 + 5.dp.toPx()
                        rotate(spin.value) {
                            drawArc(
                                Brush.sweepGradient(
                                    0f to Color.Transparent,
                                    0.55f to Color.Transparent,
                                    0.95f to Color(0xFF78B4FF).copy(alpha = ra),
                                    1f to Color.Transparent,
                                    center = center
                                ),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(center.x - ringR, center.y - ringR),
                                size = Size(ringR * 2, ringR * 2),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                    val g = 0.45f * blink.value * conn
                    if (g > 0f) {
                        drawCircle(
                            Brush.radialGradient(
                                0.55f to AccentColor.copy(alpha = 0.55f * g),
                                1f to Color.Transparent
                            )
                        )
                    }
                }
        )
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
                .background(
                    Brush.radialGradient(
                        listOf(
                            lerp(Color(0xFF6B7280), Color(0xFF5B9BFF), conn),
                            lerp(Color(0xFF3F4652), Color(0xFF2F6FE4), conn)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("OK", color = Color.White, fontSize = (22 * k).sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
}

/** Lučka, ki utripne ob vsakem pošiljanju, in vrstica stanja. */
@Composable
fun StatusBar(s: AppState) {
    val t = s.tr
    // "Še ni povezano" za trenutek, ko pritisneš tipko pred povezavo
    var notReadyFlash by remember { mutableStateOf(false) }
    LaunchedEffect(s.notReadyTick) {
        if (s.notReadyTick > 0) {
            notReadyFlash = true
            delay(1400)
            notReadyFlash = false
        }
    }
    // po 4 sekundah brez povezave: "Box ni dosegljiv"
    LaunchedEffect(s.connected, s.paired) {
        if (!s.connected && s.paired) {
            delay(4000)
            if (!s.connected) s.markUnreachable()
        }
    }
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
            Row(
                Modifier
                    .weight(1f)
                    .clickable { if (s.needRepair && !s.connected) s.resetPairing() else s.showBoxPicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    when {
                        s.connected -> t.connectedTo(s.current?.name ?: "")
                        s.needRepair -> t.needRepairMsg
                        notReadyFlash -> t.notReady
                        s.unreachable -> t.unreachable
                        else -> t.connectingTo(s.current?.name ?: "box")
                    },
                    Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    color = if (!s.connected && (notReadyFlash || s.unreachable || s.needRepair)) Color(0xFFFBBF24) else LabelColor
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = t.pickBox,
                    modifier = Modifier.size(16.dp),
                    tint = DimColor
                )
            }
        }
        IconButton(onClick = { s.showSettings = true }) {
            Icon(Icons.Filled.Settings, contentDescription = t.settings, tint = DimColor)
        }
    }
}

/** Kartica z nasvetom: zapre se samo s ✕ ali "Ne prikazuj več". */
@Composable
fun TipCard(s: AppState, modifier: Modifier) {
    val t = s.tr
    val idx = s.tipShow
    val text = t.tips.getOrNull(idx) ?: return
    val shape = RoundedCornerShape(18.dp)
    val enter = remember { Animatable(0f) }
    LaunchedEffect(idx) {
        enter.snapTo(0f)
        enter.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
    }
    val pulse = rememberInfiniteTransition(label = "tipdot")
    val dot by pulse.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "dot"
    )
    Column(
        modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = (1f - enter.value) * 160.dp.toPx()
                alpha = enter.value
            }
            .shadow(16.dp, shape, ambientColor = AccentColor, spotColor = AccentColor)
            .clip(shape)
            .background(Color(0xF514181E))
            .border(1.dp, Color(0xFF2A3240), shape)
            .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GlowColor.copy(alpha = dot))
            )
            Spacer(Modifier.width(8.dp))
            Text(t.tipTitle, fontSize = 12.sp, color = ArrowLit, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            IconButton(onClick = { s.closeTip() }) {
                Icon(Icons.Filled.Close, contentDescription = t.close, tint = DimColor, modifier = Modifier.size(18.dp))
            }
        }
        Text(text, fontSize = 14.sp, color = TextColor, lineHeight = 20.sp, modifier = Modifier.padding(end = 8.dp))
        Spacer(Modifier.height(10.dp))
        Text(
            t.tipNever,
            Modifier.clickable { s.tipsNever() },
            fontSize = 12.sp,
            color = DimColor
        )
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
                textStyle = TextStyle(color = TextColor, fontSize = 17.sp, fontFamily = Inter),
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

/** Izbira med shranjenimi boxi, dodajanje, preimenovanje in odstranjevanje. */
@Composable
fun BoxPickerDialog(s: AppState) {
    val t = s.tr
    var menuFor by remember { mutableStateOf<String?>(null) }
    var renaming by remember { mutableStateOf<BoxInfo?>(null) }
    var newName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { s.showBoxPicker = false },
        title = { Text(t.pickBox) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                s.boxes.forEach { b ->
                    val on = b.id == s.curId
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (on) Color(0xFF16233A) else Color.Transparent)
                            .border(1.5.dp, if (on) AccentColor else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { s.switchBox(b.id) }
                            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TvIcon(20.dp, if (on) GlowColor else DimColor)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(b.name, fontSize = 15.sp, color = TextColor)
                            Text(b.host, fontSize = 11.sp, color = DimColor)
                        }
                        if (on) Icon(Icons.Filled.Check, contentDescription = null, tint = GlowColor)
                        Box {
                            IconButton(onClick = { menuFor = b.id }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = null, tint = DimColor)
                            }
                            DropdownMenu(expanded = menuFor == b.id, onDismissRequest = { menuFor = null }) {
                                DropdownMenuItem(
                                    text = { Text(t.rename) },
                                    onClick = {
                                        menuFor = null
                                        newName = b.name
                                        renaming = b
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(t.remove) },
                                    onClick = {
                                        menuFor = null
                                        s.removeBox(b.id)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { s.addBox() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = ArrowLit)
                    Spacer(Modifier.width(10.dp))
                    Text(t.addBox, color = ArrowLit, fontSize = 15.sp)
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = { s.showBoxPicker = false }) { Text(t.close) }
        }
    )

    renaming?.let { b ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text(t.rename) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it.take(30) },
                    label = { Text(t.boxName) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    s.renameBox(b.id, newName)
                    renaming = null
                }) { Text(t.save) }
            },
            dismissButton = {
                OutlinedButton(onClick = { renaming = null }) { Text(t.cancel) }
            }
        )
    }
}

/** Prikaz prebranega seznama programov (za preverjanje branja sheme). */
@Composable
fun ListDialog(s: AppState) {
    val t = s.tr
    AlertDialog(
        onDismissRequest = { s.showListDialog = false },
        title = { Text("${t.chTitle} (${s.channels.size})") },
        text = {
            LazyColumn(Modifier.height(420.dp)) {
                itemsIndexed(s.channels) { _, c ->
                    Text(
                        "${c.pos}  ·  ${c.name.ifBlank { "?" }}  ·  logo ${if (c.logo.isNotBlank()) "✓" else "✗"}",
                        fontSize = 13.sp,
                        color = LabelColor,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { s.showListDialog = false }) { Text(t.close) }
        }
    )
}

/** Logotip kanala ali kratica, če logotipa ni. */
@Composable
fun ChLogo(s: AppState, c: Ch, fade: Float, h: Dp) {
    val bmp = remember(c.logo, s.logoTick) { s.logoBitmap(c.logo) }
    Box(
        Modifier
            .size(h * 1.5f, h)
            .graphicsLayer { alpha = fade },
        contentAlignment = Alignment.Center
    ) {
        if (bmp != null) {
            Image(bmp, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2A3140)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials(c.name), fontSize = 8.sp, color = TextColor, fontWeight = FontWeight.Medium, maxLines = 1)
            }
        }
    }
}

/** Višina ene vrstice v bobnu programov. */
fun drumRowH(k: Float): Dp = 28.dp * k

/** Boben programov: brez ozadja, izbrani program ima mehak sij. Vrtenje samo premika, tap preklopi. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelDrum(s: AppState, k: Float, modifier: Modifier) {
    val t = s.tr
    val list = s.channels
    val rowH = drumRowH(k)

    if (list.isEmpty()) {
        Box(
            modifier.height(rowH * 7).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (s.syncRunning) t.drumEmpty else t.syncNever,
                fontSize = (14 * k).sp, color = DimColor, textAlign = TextAlign.Center
            )
        }
        return
    }

    val state = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(state)
    val curIdx = list.indexOfFirst { it.pos == s.channel }
    // ob odprtju je boben čisto na vrhu (vidi se cela tipka TV v živo),
    // pri menjavi programa pa se zavrti na izbrani program
    var firstRun by remember { mutableStateOf(true) }
    LaunchedEffect(s.channel, list.size) {
        if (firstRun) {
            firstRun = false
            return@LaunchedEffect
        }
        if (curIdx >= 0) state.animateScrollToItem(curIdx)
    }
    val center by remember {
        derivedStateOf {
            val info = state.layoutInfo
            val mid = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - mid) }?.index ?: 0
        }
    }
    val baseText = LocalTextStyle.current
    val glowText = baseText.copy(shadow = Shadow(GlowColor.copy(alpha = 0.9f), Offset.Zero, 14f))
    // kje je prvi program (za tipko TV, ki stoji nad njim)
    val firstY by remember {
        derivedStateOf {
            val info = state.layoutInfo
            info.visibleItemsInfo.firstOrNull { it.index == 0 }?.let { it.offset - info.viewportStartOffset }
        }
    }

    // tipka TV v živo ob vrtenju ne sme seči nad svoje mesto (čez Domov in Išči)
    val topCut = (rowH * 3 - 52.dp * k) / 2
    Box(
        modifier
            .height(rowH * 7)
            .clipToBounds()
            .drawWithContent {
                clipRect(top = topCut.toPx()) { this@drawWithContent.drawContent() }
            }
    ) {
    Box(
        Modifier
            .fillMaxSize()
            // zgoraj in spodaj programi postopno izginejo v ozadje daljinca
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.22f to Color.Black,
                        0.78f to Color.Black,
                        1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        // mehak sij za izbranim programom: eliptičen, brez ostrih robov
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(rowH * 2.6f)
                .drawBehind {
                    val r = size.height / 2
                    // sredina sija (ime "center" je v bobnu že zasedeno)
                    val mid = Offset(size.width / 2, size.height / 2)
                    scale(scaleX = size.width / size.height, scaleY = 1f) {
                        drawCircle(
                            Brush.radialGradient(
                                0f to GlowColor.copy(alpha = 0.26f),
                                0.45f to GlowColor.copy(alpha = 0.10f),
                                1f to Color.Transparent,
                                center = mid,
                                radius = r
                            ),
                            radius = r,
                            center = mid
                        )
                    }
                }
        )
        LazyColumn(
            state = state,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = rowH * 3),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(list) { i, c ->
                val d = abs(i - center)
                val col = when (d) {
                    0 -> Color.White
                    1 -> LabelColor
                    else -> Color(0xFF5A616B)
                }
                val alpha = when (d) {
                    0 -> 1f
                    1 -> 0.8f
                    else -> 0.5f
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(rowH)
                        .clickable { s.pickChannel(c.pos) }
                        // kot pravi boben: izbrani je največji, proti robu so vse manjši
                        .graphicsLayer {
                            val info = state.layoutInfo
                            val item = info.visibleItemsInfo.firstOrNull { it.index == i }
                            val mid = (info.viewportStartOffset + info.viewportEndOffset) / 2f
                            val dist = if (item != null) abs(item.offset + item.size / 2f - mid) / item.size else 3f
                            val sc = (1.3f - 0.25f * dist).coerceIn(0.72f, 1.3f)
                            scaleX = sc
                            scaleY = sc
                            transformOrigin = TransformOrigin(0f, 0.5f)
                        }
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${c.pos}",
                        Modifier.width(28.dp * k),
                        fontSize = (14 * k).sp,
                        fontWeight = FontWeight.Medium,
                        color = col,
                        textAlign = TextAlign.End
                    )
                    Spacer(Modifier.width(7.dp))
                    ChLogo(s, c, alpha, 19.dp * k)
                    Spacer(Modifier.width(7.dp))
                    Text(
                        c.name,
                        fontSize = (14 * k).sp,
                        color = col,
                        style = if (d == 0) glowText else baseText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
        // nad prvim programom: tipka TV v živo (ni zatemnjena, ob vrtenju zdrsne gor)
        firstY?.let { y ->
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .height(rowH * 3)
                    .offset { IntOffset(0, y - (rowH * 3).roundToPx()) }
            ) {
                val keyW = maxWidth - 8.dp
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RoundKey(52.dp * k, onDown = { s.openLiveTv() }, width = keyW) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TvIcon(18.dp * k)
                            Spacer(Modifier.width(8.dp))
                            Text(t.liveTv, fontSize = (14 * k).sp, fontWeight = FontWeight.Medium, color = TextColor)
                        }
                    }
                }
            }
        }
    }
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
            val k = minOf(maxHeight / 870.dp, maxWidth / 360.dp).coerceIn(0.7f, 1.2f)
            val key = 50.dp * k
            val big = 62.dp * k
            val small = 52.dp * k
            val gap = 6.dp
            val vgap = 12.dp * k

            // med povezovanjem je daljinec rahlo zatemnjen
            val dim by animateFloatAsState(if (s.connected || !s.paired) 1f else 0.6f, tween(500), label = "dim")
            // nasvet: največ eden na teden, malo po povezavi
            LaunchedEffect(s.connected) {
                if (s.connected && s.tipShow < 0) {
                    val i = s.tipDue()
                    if (i >= 0) {
                        delay(1500)
                        s.tipShow = i
                    }
                }
            }
            // svetloba pod tipkami: ena skupna plast na ohišju
            val glowLayer = remember { GlowLayer() }
            val glowing = glowLayer.items.isNotEmpty()
            LaunchedEffect(glowing) {
                while (glowLayer.items.isNotEmpty()) {
                    withFrameNanos { now ->
                        glowLayer.now = now
                        glowLayer.items.removeAll { now - it.start > PULSE_NS }
                    }
                }
            }

            CompositionLocalProvider(LocalGlow provides glowLayer) {
            Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp)) {
                StatusBar(s)
                Spacer(Modifier.height(4.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(BodyBrush)
                        .graphicsLayer { alpha = dim }
                        .onGloballyPositioned { glowLayer.origin = it.positionInRoot() }
                        .drawBehind {
                            glowLayer.now
                            glowLayer.draw(this)
                        }
                        .padding(horizontal = 14.dp, vertical = 16.dp * k),
                    verticalArrangement = Arrangement.spacedBy(vgap)
                ) {
                    // vklop/izklop
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        RoundKey(48.dp * k, onDown = { s.press(26) }) {
                            PowerIcon(22.dp * k)
                        }
                    }

                    // številčnica (malo ožja od ostalega daljinca)
                    Column(
                        Modifier.fillMaxWidth(0.84f).align(Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(5.dp * k)
                    ) {
                        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(-1, 0, -1)).forEachIndexed { ri, row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                                row.forEachIndexed { ci, n ->
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

                    // glasnost (pod gumbom Nazaj) in boben programov (do desnega roba gumba Išči)
                    // tipka TV v živo stoji na sredini praznega prostora nad prvim programom;
                    // ta prazen del potegnemo gor, da je razmik do vrstice Nazaj/Domov/Išči
                    // enak kot razmik med smernim krogom in to vrstico
                    val headPad = if (s.chEnabled) (drumRowH(k) * 3 - small) / 2 else 0.dp
                    BoxWithConstraints(Modifier.fillMaxWidth().offset(y = -headPad)) {
                        val col = (maxWidth - gap * 2) / 3
                        val endPad = (col - big) / 2
                        Row(verticalAlignment = Alignment.Top) {
                            Column(
                                Modifier.width(col).padding(top = headPad),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp * k)
                            ) {
                                RoundKey(small, onDown = { s.keyDown(24) }, onUp = { s.keyUp(24) }) {
                                    Text("+", fontSize = (28 * k).sp, color = TextColor)
                                }
                                RoundKey(small, onDown = { s.volMinusDown() }, onUp = { s.volMinusUp() }) {
                                    Text("−", fontSize = (28 * k).sp, color = TextColor)
                                }
                                Text(t.volume, fontSize = (11 * k).sp, color = LabelColor)
                            }
                            Spacer(Modifier.width(gap))
                            Column(
                                Modifier.width(col * 2 + gap - endPad),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (s.chEnabled) {
                                    ChannelDrum(s, k, Modifier.fillMaxWidth())
                                    Text(t.drumHint, fontSize = (11 * k).sp, color = LabelColor)
                                } else {
                                    // brez seznama programov: TV v živo (ob +) in Videoklub (ob −)
                                    val keyW = col * 2 + gap - endPad - 8.dp
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp * k)) {
                                        RoundKey(small, onDown = { s.openLiveTv() }, width = keyW) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TvIcon(18.dp * k)
                                                Spacer(Modifier.width(8.dp))
                                                Text(t.liveTv, fontSize = (14 * k).sp, fontWeight = FontWeight.Medium, color = TextColor)
                                            }
                                        }
                                        RoundKey(small, onDown = { s.openVod() }, width = keyW) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                FilmIcon(18.dp * k)
                                                Spacer(Modifier.width(8.dp))
                                                Text(t.vod, fontSize = (14 * k).sp, fontWeight = FontWeight.Medium, color = TextColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }
        }


        // nasvet, ki se pripelje od spodaj
        if (s.tipShow >= 0 && !s.searchOpen) {
            TipCard(
                s,
                Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            )
        }

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
