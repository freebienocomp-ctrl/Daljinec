package si.daljinec

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.util.Date
import java.util.concurrent.Executors

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
            step(Key.UP, Timing.MENU_STEP_MS)          // gor -> mikrofon
            step(Key.RIGHT, Timing.MENU_STEP_MS)       // desno -> jezik
            step(Key.OK)               // OK -> seznam jezikov
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
                if (running && g == gen) Thread.sleep(Timing.RECONNECT_DELAY_MS)
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
        val fresh = list.filter { now - it.second < Timing.QUEUE_MAX_AGE_MS }
        if (fresh.isEmpty()) return
        keys.execute {
            for ((code, _) in fresh) {
                remote?.key(code)
                Thread.sleep(Timing.VOLUME_REPLAY_GAP_MS)
            }
        }
    }

    /** Če box že dolgo ni nič poslal, je povezava verjetno mrtva: začni znova. */
    fun ensureFresh() {
        val r = remote ?: return
        if (running && System.currentTimeMillis() - r.lastRx > Timing.STALE_CONNECTION_MS) r.close()
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
        keys.execute { remote?.key(code, KeyDir.SHORT) }
    }

    /** Pravo držanje: tipka pritisnjena. */
    fun keyDown(code: Int) {
        if (!connected && paired) {
            if (code == Key.VOL_UP) queueVolume(Key.VOL_UP) else blocked()
            return
        }
        closeSearch()
        ledTick++
        keys.execute { remote?.key(code, KeyDir.DOWN) }
    }

    /** Pravo držanje: tipka spuščena. */
    fun keyUp(code: Int) {
        if (!connected) return
        keys.execute { remote?.key(code, KeyDir.UP) }
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


    // glasnost −: kratek pritisk zniža, dolg pritisk (0,6 s) utiša
    private var minusMuted = false
    private val muteRunnable = Runnable {
        minusMuted = true
        if (!connected && paired) {
            queueVolume(Key.MUTE)
        } else {
            ledTick++
            keys.execute { remote?.key(Key.MUTE) }
        }
    }

    fun volMinusDown() {
        closeSearch()
        minusMuted = false
        main.postDelayed(muteRunnable, Timing.LONG_PRESS_MUTE_MS)
    }

    fun volMinusUp() {
        main.removeCallbacks(muteRunnable)
        if (!minusMuted) {
            if (!connected && paired) queueVolume(Key.VOL_DOWN) else press(Key.VOL_DOWN)
        }
    }

    /** Številka s številčnice: pošlje tipko in si zapomni program. */
    fun digit(n: Int) {
        if (blocked()) return
        val now = System.currentTimeMillis()
        entry = if (now - lastDigit < 2000 && entry.length < 4) entry + n else n.toString()
        lastDigit = now
        entry.toIntOrNull()?.let { if (it > 0) saveChannel(it) }
        press(Key.DIGIT_0 + n)
    }

    /** Tipka Videoklub: Domov, desno, desno, OK. */
    fun openVod() {
        if (blocked()) return
        closeSearch()
        ledTick++
        keys.execute {
            kbOpen = false
            step(Key.HOME, homeWait.toLong())
            step(Key.RIGHT, Timing.MENU_STEP_MS)
            step(Key.RIGHT, Timing.SETTLE_BEFORE_OK_MS)   // izbira se mora umiriti, sicer box OK prezre
            step(Key.OK)
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
            step(Key.HOME, homeWait.toLong())
            step(Key.RIGHT, Timing.MENU_STEP_MS)
            step(Key.DOWN, Timing.SETTLE_BEFORE_OK_MS)   // izbira se mora umiriti, sicer box OK prezre
            step(Key.OK)
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
                remote?.key(Key.DIGIT_0 + (ch - '0'))
                Thread.sleep(Timing.DIGIT_GAP_MS)
            }
            remote?.key(Key.OK)
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
        while (kr < tr) { step(Key.DOWN); kr++ }
        while (kr > tr) { step(Key.UP); kr-- }
        while (kc < tc) { step(Key.RIGHT); kc++ }
        while (kc > tc) { step(Key.LEFT); kc-- }
    }

    private fun openSearchIfNeeded() {
        if (kbOpen) return
        step(Key.HOME, homeWait.toLong())     // Domov, počakaj, da se naloži
        step(Key.LEFT)                       // levo -> lupa
        step(Key.LEFT)                       // levo (zavarovanje)
        step(Key.LEFT, screenWait.toLong())  // levo (zavarovanje), odpre iskanje
        step(Key.DOWN)                       // dol -> mikrofon
        step(Key.DOWN)                       // dol -> A
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
                step(Key.UP, Timing.MENU_STEP_MS)
                kr = 0
            }
            repeat(del) { step(Key.OK, press) }
        }
        if (add.isNotEmpty() && kr == 0) {
            // z brisanja nazaj na tipkovnico, na A
            step(Key.DOWN, Timing.MENU_STEP_MS)
            repeat(5) { step(Key.LEFT) }
            kr = 1
            kc = 0
        }
        for ((tr, tc) in add) {
            if (tr == -1) {
                // presledek: z vrstice Ž, stolpec 2, en korak dol
                moveTo(5, 2)
                step(Key.DOWN)
                step(Key.OK, press)
                step(Key.UP)
                repeat(5) { step(Key.LEFT) }
                kr = 5
                kc = 0
                continue
            }
            moveTo(tr, tc)
            step(Key.OK, press)
        }
    }
}
