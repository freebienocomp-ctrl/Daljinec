package si.daljinec

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Preverja EON tipkovnico, seznam programov in shranjene boxe. */
class DataTest {

    @Test
    fun crkeNaSlovenskiTipkovnici() {
        assertEquals(Pair(1, 0), eonPos('a', "sl"))
        assertEquals(Pair(1, 3), eonPos('Č', "sl"))
        assertEquals(Pair(5, 0), eonPos('ž', "sl"))
        assertEquals(Pair(-1, -1), eonPos(' ', "sl"))
        assertNull(eonPos('#', "sl"))
    }

    @Test
    fun manjkajocaCrkaVzamePodobno() {
        // angleška tipkovnica nima Č, zato vzame C
        assertEquals(eonPos('C', "en"), eonPos('Č', "en"))
    }

    @Test
    fun neznanJezikUporabiSlovenskoTipkovnico() {
        assertEquals(eonPos('Š', "sl"), eonPos('Š', "xx"))
    }

    @Test
    fun vsakaVrsticaTipkovniceImaSestZnakov() {
        EON_LAYOUTS.forEach { (lang, rows) ->
            assertEquals(lang, 5, rows.size)
            rows.forEach { assertEquals("$lang: $it", 6, it.length) }
        }
    }

    @Test
    fun programiSeShranijoInPreberejo() {
        val list = listOf(Ch(1, "SLO 1", "https://x/1.png"), Ch(101, "POP TV", ""))
        assertEquals(list, channelsFromJson(channelsToJson(list)))
    }

    @Test
    fun programBrezPozicijeSeIzpusti() {
        val t = """[{"p":0,"n":"A"},{"p":5,"n":" B "}]"""
        assertEquals(listOf(Ch(5, "B", "")), channelsFromJson(t))
    }

    @Test
    fun pokvarjenSeznamVrnePraznega() {
        assertTrue(channelsFromJson("to ni json").isEmpty())
        assertTrue(boxesFromJson("").isEmpty())
    }

    @Test
    fun boxiSeShranijoInPreberejo() {
        val list = listOf(
            BoxInfo("a1", "Dnevna", "192.168.1.20", "sl", true),
            BoxInfo("b2", "Spalnica", "192.168.1.21", "en", false)
        )
        assertEquals(list, boxesFromJson(boxesToJson(list)))
    }

    @Test
    fun boxBrezPodatkovDobiPrivzeteVrednosti() {
        assertEquals(listOf(BoxInfo("x", "Box", "", "sl", false)), boxesFromJson("""[{"id":"x"}]"""))
    }

    @Test
    fun kraticaZaProgramBrezLogotipa() {
        assertEquals("POP", initials("POP TV"))
        assertEquals("SLO", initials("SLO 1"))
        assertEquals("DI", initials("discovery"))
    }

    @Test
    fun vsiJezikiAplikacijeObstajajo() {
        assertTrue(TR.keys.containsAll(listOf("sl", "en", "sr")))
    }
}
