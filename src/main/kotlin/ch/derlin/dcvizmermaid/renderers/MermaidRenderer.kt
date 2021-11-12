package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.graph.GraphTheme
import java.util.*

class MermaidRenderer(graph: String, theme: GraphTheme) {

    private val payloadBase64 by lazy { toMermaidBase64(graph, theme) }

    fun getEditorLink() = "${Config.mermaidLiveEditorUrl}/edit#$payloadBase64"
    fun getPreviewLink() = "${Config.mermaidLiveEditorUrl}/view/#$payloadBase64"

    fun getPngLink() = "${Config.mermaidInkUrl}/img/$payloadBase64"
    fun getSvgLink() = "${Config.mermaidInkUrl}/svg/$payloadBase64"

    companion object {

        fun toMermaidBase64(graph: String, theme: GraphTheme = GraphTheme.DEFAULT): String {
            val escapedCode = graph.replace("\"", "\\\"").replace("\n", "\\n")
            val data = "{\"code\":\"$escapedCode\"," +
                    "\"mermaid\": {\"theme\": \"${theme.name.lowercase()}\"},\"updateEditor\":true,\"autoSync\":true,\"updateDiagram\":true}"
            return Base64.getEncoder().encodeToString(data.toByteArray()).trimEnd('=')
        }
    }
}