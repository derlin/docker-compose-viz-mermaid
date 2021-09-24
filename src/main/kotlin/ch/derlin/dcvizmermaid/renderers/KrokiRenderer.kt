package ch.derlin.dcvizmermaid.renderers

import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.zip.Deflater

object KrokiRenderer {

    const val krokiBaseUrl = "https://kroki.io/mermaid"

    fun getSvgLink(graph: String) = "$krokiBaseUrl/svg/${encode(graph)}"


    @Throws(IOException::class)
    fun encode(decoded: String): String = compress(decoded.toByteArray()).let {
        String(Base64.getUrlEncoder().encode(it), Charset.forName("utf-8"))
    }

    @Throws(IOException::class)
    private fun compress(source: ByteArray): ByteArray {
        // see https://github.com/DaveJarvis/keenwrite/issues/138#issuecomment-922562707
        val deflater = Deflater()
        deflater.setInput(source)
        deflater.finish()
        val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
        val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
        val returnValues = ByteArray(numberOfBytesAfterCompression)
        System.arraycopy(bytesCompressed, 0, returnValues, 0, numberOfBytesAfterCompression)
        return returnValues
    }
}