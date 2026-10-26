package si.daljinec

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

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
