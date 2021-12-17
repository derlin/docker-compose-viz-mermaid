package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.graph.GraphTheme
import java.io.File
import java.net.URL

interface Renderer {
    fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String
    fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String

    fun String.downloadTo(outputFile: File): String {
        URL(this).openStream().transferTo(outputFile.outputStream())
        println("Saved image to $outputFile")
        return outputFile.absolutePath
    }
}

interface Previewer {
    fun getEditorLink(graph: String, theme: GraphTheme): String
    fun getPreviewLink(graph: String, theme: GraphTheme): String
}