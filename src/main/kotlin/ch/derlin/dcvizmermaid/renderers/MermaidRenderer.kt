package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.graph.GraphTheme
import java.io.File
import java.util.*

object MermaidRenderer : Renderer, Previewer {

    override fun getEditorLink(graph: String, theme: GraphTheme) =
        "${Config.mermaidLiveEditorUrl}/edit#${toMermaidBase64(graph, theme)}"

    override fun getPreviewLink(graph: String, theme: GraphTheme) =
        "${Config.mermaidLiveEditorUrl}/view/#${toMermaidBase64(graph, theme)}"

    override fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?) =
        "${Config.mermaidInkUrl}/img/${toMermaidBase64(graph, theme)}".appendBgColor(bgColor).downloadTo(outputFile)

    override fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?) =
        "${Config.mermaidInkUrl}/svg/${toMermaidBase64(graph, theme)}".appendBgColor(bgColor).downloadTo(outputFile)

    private fun String.appendBgColor(bgColor: String?) = bgColor
        ?.let { if (it.startsWith("#")) it.drop(1) else "!$it" } // named colors prefixed with "!", hex color without "#"
        ?.let { "$this?bgColor=$it" }
        ?: this

    private fun toMermaidBase64(graph: String, theme: GraphTheme = GraphTheme.DEFAULT): String {
        val escapedCode = graph.replace("\"", "\\\"").replace("\n", "\\n")
        val data = "{\"code\":\"$escapedCode\"," +
                "\"mermaid\": {\"theme\": \"${theme.name.lowercase()}\"},\"updateEditor\":true,\"autoSync\":true,\"updateDiagram\":true}"
        return Base64.getEncoder().encodeToString(data.toByteArray()).trimEnd('=')
    }
}
