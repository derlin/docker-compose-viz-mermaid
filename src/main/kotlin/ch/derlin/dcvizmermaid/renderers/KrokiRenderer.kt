package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.graph.GraphTheme
import java.io.File
import java.util.*
import java.util.zip.Deflater

object KrokiRenderer : Renderer {

    override fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String =
        throw NotImplementedError("kroki.io doesn't support PNG rendering yet")
        //"${Config.krokiUrl}/mermaid/png/${encode(graph)}".downloadTo(outputFile)

    override fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String =
        "${Config.krokiUrl}/mermaid/svg/${encode(graph)}".downloadTo(outputFile)


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