package si.daljinec

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
            RoundKey(52.dp, onDown = { s.press(Key.UP) }) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = t.up, modifier = Modifier.size(28.dp), tint = TextColor)
            }
            RoundKey(52.dp, onDown = { s.press(Key.OK) }, brush = Brush.verticalGradient(listOf(Color(0xFF5B9BFF), Color(0xFF2F6FE4)))) {
                Text("OK", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            RoundKey(52.dp, onDown = { s.press(Key.DOWN) }) {
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
