package ch.derlin.dcvizmermaid

object Config {

    /** Override the mermaid.ink URL used to generate PNG/SVG */
    val mermaidInkUrl by lazy {
        get("MERMAID_INK_URL", "https://mermaid.ink")
    }

    /** Override the live editor base URL for preview and web edit */
    val mermaidLiveEditorUrl by lazy {
        get("MERMAID_LIVE_EDITOR_URL", "https://mermaid-js.github.io/mermaid-live-editor")
    }

    /** Override the kroki URL (currently unused) */
    val krokiUrl by lazy {
        get("KROKI_URL", "https://kroki.io")
    }

    private fun get(env: String, defaultValue: String) = System.getenv().getOrDefault(env, defaultValue)
}
