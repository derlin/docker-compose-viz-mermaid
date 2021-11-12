package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.Config
import java.util.*
import java.util.zip.Deflater

object KrokiRenderer {

    fun getSvgLink(graph: String) = "${Config.krokiUrl}/mermaid/svg/${encode(graph)}"
    fun getPngLink(graph: String) = "${Config.krokiUrl}/mermaid/png/${encode(graph)}"

    private fun encode(decoded: String): String =
        String(Base64.getUrlEncoder().encode(compress(decoded.toByteArray())), Charsets.UTF_8)

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