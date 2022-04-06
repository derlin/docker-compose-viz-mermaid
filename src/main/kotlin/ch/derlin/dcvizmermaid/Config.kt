package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.renderers.KrokiRenderer
import ch.derlin.dcvizmermaid.renderers.LocalRenderer
import ch.derlin.dcvizmermaid.renderers.MermaidRenderer
import ch.derlin.dcvizmermaid.renderers.Renderer

object Config {

    /** Override the mermaid.ink URL used to generate PNG/SVG */
    val mermaidInkUrl: String by lazy {
        get("MERMAID_INK_URL", "https://mermaid.ink")
    }

    /** Override the live editor base URL for preview and web edit */
    val mermaidLiveEditorUrl: String by lazy {
        get("MERMAID_LIVE_EDITOR_URL", "https://mermaid-js.github.io/mermaid-live-editor")
    }

    /** Override the kroki URL (currently unused) */
    val krokiUrl: String by lazy {
        get("KROKI_URL", "https://kroki.io")
    }

    /** Choose the implementation to use for rendering PNG or SVG */
    val renderer: Renderer by lazy {
        when (get("MERMAID_RENDERER", "")) {
            "mermaid.ink" -> MermaidRenderer
            "kroki.io" -> KrokiRenderer
            else -> LocalRenderer
        }
    }

    private fun get(env: String, defaultValue: String) = System.getenv().getOrDefault(env, defaultValue)
}
