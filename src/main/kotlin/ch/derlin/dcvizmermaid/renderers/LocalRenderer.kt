package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.graph.GraphTheme
import com.microsoft.playwright.Browser
import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import java.io.File

object LocalRenderer : Renderer {

    const val VIEWPORT_WIDTH = 1280
    const val VIEWPORT_HEIGHT = 1024
    const val VIEWPORT_SCALE_FACTOR = 1.2 // improve a bit the quality

    override fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String {
        renderCodeOnPage(graph, theme.bgColor()) { page ->
            page.querySelector("svg")
                .screenshot(ElementHandle.ScreenshotOptions().setPath(outputFile.toPath()))
        }
        return outputFile.absolutePath.toString()
    }

    override fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String {
        renderCodeOnPage(graph, theme.bgColor()) { page ->
            page.querySelector(".mermaid").innerHTML().let { svgContent ->
                outputFile.writeText(svgContent)
            }
        }
        return outputFile.absolutePath.toString()
    }

    private fun renderCodeOnPage(mermaidCode: String, bgColor: String? = null, block: (Page) -> Unit) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch().newContext(
                Browser.NewContextOptions()
                    .setViewportSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                    .setDeviceScaleFactor(VIEWPORT_SCALE_FACTOR)
            )
            val page: Page = browser.newPage()
            page.setContent(HtmlPageRenderingMermaid(mermaidCode))
            // page.locator("svg").waitFor()
            bgColor?.let {
                page.evalOnSelector("svg", "elt => elt.style.backgroundColor = '$bgColor'")
            }
            block(page)
        }
    }

    private object HtmlPageRenderingMermaid {

        operator fun invoke(mermaidCode: String): String = HTML_TEMPLATE
            .replace(MERMAID_JS_PLACEHOLDER, mermaidJsCode)
            .replace(CODE_PLACEHOLDER, mermaidCode)

        private val mermaidJsCode: String by lazy {
            requireNotNull(javaClass.classLoader.getResource("mermaid.min.js")) {
                "Could not load mermaid.min.js from resources"
            }.readText()
        }

        private const val MERMAID_JS_PLACEHOLDER = "@MERMAID_MIN_JS@"
        private const val CODE_PLACEHOLDER = "@MERMAID_CODE@"
        private const val HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8" />
            <title>mermaid</title>
        </head>
        <body>
        <div id="container"></div>
        <div class="mermaid">$CODE_PLACEHOLDER</div>
        <script>$MERMAID_JS_PLACEHOLDER</script>
        <script>
        mermaid.initialize({
            startOnLoad:true
        });
        </script>
        </body>
        </html>
        """
    }
}
