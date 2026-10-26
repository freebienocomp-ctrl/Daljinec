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
                        if (held < Timing.MIN_PRESS_VISIBLE_MS) delay(Timing.MIN_PRESS_VISIBLE_MS - held)
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
                        s.press(Key.OK)
                        scope.launch {
                            okFace.snapTo(0f)
                            okFace.animateTo(1f, tween(420, easing = LinearEasing))
                        }
                        scope.launch { okScale.animateTo(0.93f, tween(70)) }
                        tryAwaitRelease()
                        scope.launch {
                            val held = System.currentTimeMillis() - t0
                            if (held < Timing.MIN_PRESS_VISIBLE_MS) delay(Timing.MIN_PRESS_VISIBLE_MS - held)
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
                        val code = intArrayOf(Key.UP, Key.RIGHT, Key.DOWN, Key.LEFT)[dir]
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
                            if (held < Timing.MIN_PRESS_VISIBLE_MS) delay(Timing.MIN_PRESS_VISIBLE_MS - held)
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
        // vrteč se krog okoli OK med povezovanjem
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
            delay(Timing.UNREACHABLE_AFTER_MS)
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
