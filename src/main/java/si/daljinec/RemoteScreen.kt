package si.daljinec

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
                        delay(Timing.TIP_DELAY_MS)
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
                        RoundKey(48.dp * k, onDown = { s.press(Key.POWER) }) {
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
                            RoundKey(big, onDown = { s.press(Key.BACK) }) { IconLabel(Icons.Filled.ArrowBack, t.back, k) }
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            RoundKey(big, onDown = { s.press(Key.HOME) }) { IconLabel(Icons.Filled.Home, t.home, k) }
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
                                RoundKey(small, onDown = { s.keyDown(Key.VOL_UP) }, onUp = { s.keyUp(Key.VOL_UP) }) {
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
