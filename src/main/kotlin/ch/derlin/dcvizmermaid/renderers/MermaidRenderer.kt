package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.graph.GraphTheme
import java.util.*

class MermaidRenderer(graph: String, theme: GraphTheme) {

    private val payloadBase64 by lazy { toMermaidBase64(graph, theme) }

    fun getEditorLink() = "$mermaidLiveEditorBaseUrl/edit#$payloadBase64"
    fun getPreviewLink() = "$mermaidLiveEditorBaseUrl/view/#$payloadBase64"
    fun getPngLink() = "$mermaidRendererBaseUrl/img/$payloadBase64"
    fun getSvgLink() = "$mermaidRendererBaseUrl/svg/$payloadBase64"

    companion object {

        const val mermaidLiveEditorBaseUrl = "https://mermaid-js.github.io/mermaid-live-editor"
        const val mermaidRendererBaseUrl = "https://mermaid.ink"

        fun toMermaidBase64(graph: String, theme: GraphTheme = GraphTheme.DEFAULT): String {
            val escapedCode = graph.replace("\"", "\\\"").replace("\n", "\\n")
            val data = "{\"code\":\"$escapedCode\"," +
                    "\"mermaid\": {\"theme\": \"${theme.name.lowercase()}\"},\"updateEditor\":true,\"autoSync\":true,\"updateDiagram\":true}"
            return Base64.getEncoder().encodeToString(data.toByteArray()).trimEnd('=')
        }
    }
}