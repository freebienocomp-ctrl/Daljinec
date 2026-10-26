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
