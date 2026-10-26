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
