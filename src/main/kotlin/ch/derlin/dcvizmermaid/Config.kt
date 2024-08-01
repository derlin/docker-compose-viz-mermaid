package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.renderers.KrokiRenderer
import ch.derlin.dcvizmermaid.renderers.LocalRenderer
import ch.derlin.dcvizmermaid.renderers.MermaidRenderer
import ch.derlin.dcvizmermaid.renderers.MmdcRenderer
import ch.derlin.dcvizmermaid.renderers.Renderer
import mu.KotlinLogging
import java.io.File

object Config {
    private val logger = KotlinLogging.logger {}

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
            "mermaid-cli" -> MmdcRenderer
            else -> LocalRenderer
        }.also {
            logger.info { "Using renderer: ${it.javaClass.simpleName}" }
        }
    }

    val mmdcLocalInstallPath: String by lazy {
        get("MMDC_INSTALL_PATH", pathJoin(System.getProperty("user.home"), ".mmdc"))
    }

    val mmdcExecutable: String by lazy {
        get("MMDC_EXECUTABLE_PATH", pathJoin(mmdcLocalInstallPath, "node_modules", ".bin", "mmdc"))
    }

    const val MMDC_VERSION: String = "@mermaid-js/mermaid-cli@^10"

    private fun get(
        env: String,
        defaultValue: String,
    ) = System.getProperty(env, System.getenv().getOrDefault(env, defaultValue))

    internal fun pathJoin(vararg segments: String): String = segments.joinToString(File.separator)
}
