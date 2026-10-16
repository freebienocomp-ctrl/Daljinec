package si.daljinec

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder

// ---------- minimalni protobuf ----------

class PbWriter {
    private val out = ByteArrayOutputStream()

    private fun varint(value: Long) {
        var x = value
        while ((x and 0x7FL.inv()) != 0L) {
            out.write(((x and 0x7FL) or 0x80L).toInt())
            x = x ushr 7
        }
        out.write(x.toInt())
    }

    fun int(field: Int, v: Int): PbWriter {
        varint((field shl 3).toLong())
        varint(v.toLong())
        return this
    }

    fun bytes(field: Int, b: ByteArray): PbWriter {
        varint(((field shl 3) or 2).toLong())
        varint(b.size.toLong())
        out.write(b, 0, b.size)
        return this
    }

    fun str(field: Int, s: String): PbWriter = bytes(field, s.toByteArray(Charsets.UTF_8))
    fun msg(field: Int, m: PbWriter): PbWriter = bytes(field, m.toByteArray())
    fun toByteArray(): ByteArray = out.toByteArray()
}

class PbReader(private val data: ByteArray) {
    private val fields = HashMap<Int, MutableList<Any>>()
    private var pos = 0

    init {
        parse()
    }

    private fun readVarint(): Long {
        var result = 0L
        var shift = 0
        while (pos < data.size) {
            val b = data[pos++].toInt() and 0xFF
            result = result or ((b and 0x7F).toLong() shl shift)
            if ((b and 0x80) == 0) return result
            shift += 7
        }
        return result
    }

    private fun add(f: Int, v: Any) {
        fields.getOrPut(f) { mutableListOf() }.add(v)
    }

    private fun parse() {
        while (pos < data.size) {
            val tag = readVarint().toInt()
            val field = tag ushr 3
            when (tag and 7) {
                0 -> add(field, readVarint())
                2 -> {
                    val len = readVarint().toInt()
                    if (len < 0 || pos + len > data.size) return
                    add(field, data.copyOfRange(pos, pos + len))
                    pos += len
                }
                1 -> pos += 8
                5 -> pos += 4
                else -> return
            }
        }
    }

    fun has(f: Int): Boolean = fields.containsKey(f)
    fun long(f: Int): Long? = fields[f]?.firstOrNull() as? Long
    fun sub(f: Int): PbReader? = (fields[f]?.firstOrNull() as? ByteArray)?.let { PbReader(it) }
}

object Frames {
    fun write(out: OutputStream, data: ByteArray) {
        var n = data.size
        val hdr = ByteArrayOutputStream()
        while (true) {
            if ((n and 0x7F.inv()) == 0) {
                hdr.write(n)
                break
            }
            hdr.write((n and 0x7F) or 0x80)
            n = n ushr 7
        }
        out.write(hdr.toByteArray())
        out.write(data)
        out.flush()
    }

    fun read(inp: InputStream): ByteArray? {
        var len = 0
        var shift = 0
        while (true) {
            val b = inp.read()
            if (b < 0) return null
            len = len or ((b and 0x7F) shl shift)
            if ((b and 0x80) == 0) break
            shift += 7
        }
        val buf = ByteArray(len)
        var off = 0
        while (off < len) {
            val r = inp.read(buf, off, len - off)
            if (r < 0) return null
            off += r
        }
        return buf
    }
}

// ---------- certifikat in TLS ----------

private class FixedKeyManager(
    private val key: PrivateKey,
    private val chain: Array<X509Certificate>
) : X509KeyManager {
    override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf("client")
    override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = "client"
    override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String>? = null
    override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String? = null
    override fun getCertificateChain(alias: String?): Array<X509Certificate> = chain
    override fun getPrivateKey(alias: String?): PrivateKey = key
}

object Identity {
    private val PASS = "daljinec".toCharArray()

    fun load(ctx: Context): KeyStore {
        val file = File(ctx.filesDir, "client.p12")
        val ks = KeyStore.getInstance("PKCS12")
        if (file.exists()) {
            file.inputStream().use { ks.load(it, PASS) }
            return ks
        }
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val kp = kpg.generateKeyPair()
        val name = X500Name("CN=daljinec")
        val now = System.currentTimeMillis()
        val builder = JcaX509v3CertificateBuilder(
            name,
            BigInteger.valueOf(now),
            Date(now - 86400000L),
            Date(now + 20L * 365L * 86400000L),
            name,
            kp.public
        )
        val signer = JcaContentSignerBuilder("SHA256withRSA").build(kp.private)
        val cert = JcaX509CertificateConverter().getCertificate(builder
