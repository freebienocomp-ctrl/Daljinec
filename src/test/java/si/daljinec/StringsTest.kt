package si.daljinec

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/** Preverja, da imajo vsi trije jeziki enaka besedila v strings.xml. */
class StringsTest {

    private val dirs = listOf("values", "values-sl", "values-sr")

    private fun file(dir: String): File {
        val f = listOf("src/main/res/$dir/strings.xml", "../src/main/res/$dir/strings.xml")
            .map { File(it) }.firstOrNull { it.exists() }
        return f ?: error("Ni datoteke $dir/strings.xml")
    }

    /** ime -> besedilo (pri seznamu: vse vrstice skupaj) */
    private fun read(dir: String): Map<String, List<String>> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file(dir))
        val out = LinkedHashMap<String, List<String>>()
        val strings = doc.getElementsByTagName("string")
        for (i in 0 until strings.length) {
            val e = strings.item(i) as Element
            out[e.getAttribute("name")] = listOf(e.textContent)
        }
        val arrays = doc.getElementsByTagName("string-array")
        for (i in 0 until arrays.length) {
            val e = arrays.item(i) as Element
            val items = e.getElementsByTagName("item")
            out[e.getAttribute("name")] = (0 until items.length).map { items.item(it).textContent }
        }
        return out
    }

    private fun placeholders(s: String) = Regex("%\\d\\$[sd]").findAll(s).map { it.value }.toSet()

    @Test
    fun vsiJezikiImajoIstaBesedila() {
        val base = read("values")
        for (dir in dirs) {
            val other = read(dir)
            assertEquals("$dir: imena besedil", base.keys, other.keys)
            base.forEach { (name, v) ->
                assertEquals("$dir/$name: število vrstic", v.size, other.getValue(name).size)
            }
        }
    }

    @Test
    fun nobenoBesediloNiPrazno() {
        for (dir in dirs) read(dir).forEach { (name, v) ->
            v.forEach { assertTrue("$dir/$name je prazen", it.trim('"').isNotBlank()) }
        }
    }

    @Test
    fun vstavljeneVrednostiSoVVsehJezikih() {
        val base = read("values")
        for (dir in dirs) {
            val other = read(dir)
            base.forEach { (name, v) ->
                assertEquals("$dir/$name: %1\$s ...", placeholders(v[0]), placeholders(other.getValue(name)[0]))
            }
        }
    }

    @Test
    fun nasvetovJeSedem() {
        for (dir in dirs) assertEquals(dir, 7, read(dir).getValue("tips").size)
    }
}
