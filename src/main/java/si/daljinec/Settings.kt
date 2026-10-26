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

// ---------- nastavitve ----------

@Composable
fun SettingsScreen(s: AppState) {
    var advanced by remember { mutableStateOf(false) }
    var searchOpen by remember { mutableStateOf(false) }
    var stepText by remember { mutableStateOf(s.stepDelay.toString()) }
    var homeText by remember { mutableStateOf(s.homeWait.toString()) }
    var screenText by remember { mutableStateOf(s.screenWait.toString()) }
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { searchOpen = !searchOpen }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(t.searchOnTv, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    if (searchOpen) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = DimColor
                )
            }
            if (searchOpen) {
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
            }

            if (s.lastError.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text("${t.lastErr}: ${s.lastError}", color = DimColor, fontSize = 12.sp)
            }
        }
    }
}
