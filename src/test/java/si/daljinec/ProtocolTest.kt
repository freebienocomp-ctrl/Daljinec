package si.daljinec

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Preverja sporočila, ki jih aplikacija pošilja boxu in bere od njega. */
class ProtocolTest {

    @Test
    fun tipkaOkJePravilnoZapisana() {
        // polje 10 { koda = 23, smer = 3 }
        val b = PbWriter().msg(10, PbWriter().int(1, Key.OK).int(2, KeyDir.SHORT)).toByteArray()
        assertArrayEquals(byteArrayOf(0x52, 4, 0x08, 23, 0x10, 3), b)
    }

    @Test
    fun zapisanoSporociloSePrebereNazaj() {
        val b = PbWriter().msg(10, PbWriter().int(1, Key.MUTE).int(2, KeyDir.DOWN)).toByteArray()
        val key = PbReader(b).sub(10)!!
        assertEquals(164L, key.long(1))
        assertEquals(1L, key.long(2))
    }

    @Test
    fun pingOdBoxaSePravilnoPrebere() {
        // box pošlje polje 8 { 1 = številka }, odgovorimo s poljem 9 in isto številko
        val ping = PbWriter().msg(8, PbWriter().int(1, 123456)).toByteArray()
        val r = PbReader(ping)
        assertEquals(true, r.has(8))
        assertEquals(123456L, r.sub(8)!!.long(1))
    }

    @Test
    fun besediloSeZapiseKotUtf8() {
        val r = PbReader(PbWriter().str(1, "Čšž").toByteArray())
        assertEquals(true, r.has(1))
    }

    @Test
    fun pokvarjenoSporociloNeSesuje() {
        PbReader(byteArrayOf(0x52, 100, 1, 2))   // dolžina večja od podatkov
        PbReader(byteArrayOf(-1, -1, -1))         // nesmiselni bajti
        PbReader(ByteArray(0))
    }

    @Test
    fun okvirSePrebereNazaj() {
        for (size in listOf(0, 5, 127, 128, 300, 20000)) {
            val data = ByteArray(size) { (it % 251).toByte() }
            val out = ByteArrayOutputStream()
            Frames.write(out, data)
            assertArrayEquals(data, Frames.read(ByteArrayInputStream(out.toByteArray())))
        }
    }

    @Test
    fun nedokoncanOkvirVrneNull() {
        val out = ByteArrayOutputStream()
        Frames.write(out, ByteArray(50))
        val cut = out.toByteArray().copyOf(20)
        assertNull(Frames.read(ByteArrayInputStream(cut)))
        assertNull(Frames.read(ByteArrayInputStream(ByteArray(0))))
    }

    @Test
    fun kodeTipkSoTakeKotJihPricakujeBox() {
        assertEquals(3, Key.HOME)
        assertEquals(4, Key.BACK)
        assertEquals(19, Key.UP)
        assertEquals(20, Key.DOWN)
        assertEquals(21, Key.LEFT)
        assertEquals(22, Key.RIGHT)
        assertEquals(23, Key.OK)
        assertEquals(24, Key.VOL_UP)
        assertEquals(25, Key.VOL_DOWN)
        assertEquals(26, Key.POWER)
        assertEquals(164, Key.MUTE)
        assertEquals(7, Key.DIGIT_0)
    }
}
