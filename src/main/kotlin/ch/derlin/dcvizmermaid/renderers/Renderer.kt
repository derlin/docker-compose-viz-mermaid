package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.graph.GraphTheme
import mu.KotlinLogging
import java.io.File
import java.net.URL

private val logger = KotlinLogging.logger {}

interface Renderer {

    fun save(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String =
        when (outputFile.extension) {
            "png" -> savePng(outputFile, graph, theme, bgColor)
            "svg" -> saveSvg(outputFile, graph, theme, bgColor)
            else -> error("Unknown extension for output file $outputFile")
        }

    fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String
    fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String

    fun String.downloadTo(outputFile: File): String {
        logger.debug { "Fetching from URL: $this" }
        with(URL(this).openConnection()) {
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
            )
            getInputStream().transferTo(outputFile.outputStream())
        }
        println("Saved image to $outputFile")
        return outputFile.absolutePath
    }
}

interface Previewer {
    fun getEditorLink(graph: String, theme: GraphTheme): String
    fun getPreviewLink(graph: String, theme: GraphTheme): String
}
