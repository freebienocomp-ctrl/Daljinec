package si.daljinec

import android.content.Context
import android.os.Build
import android.media.AudioAttributes
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View

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
