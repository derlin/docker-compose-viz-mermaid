package ch.derlin.dc2mermaid.graph

import java.io.File
import java.net.URL
import java.nio.file.Path
import java.util.*

enum class MermaidOutput {
    TEXT, EDITOR, PREVIEW, PNG;

    fun process(mermaidGraph: String, outputFile: Path? = null) {
        when (this) {
            TEXT -> outputFile.print(mermaidGraph)
            EDITOR -> println("https://mermaid-js.github.io/mermaid-live-editor/edit#${mermaidGraph.toBase64()}")
            PREVIEW -> println("https://mermaid-js.github.io/mermaid-live-editor/view/#${mermaidGraph.toBase64()}")
            PNG -> println("Saved image to ${download("https://mermaid.ink/img/${mermaidGraph.toBase64()}", outputFile)}")
        }
    }

    private fun Path?.print(content: String) {
        this?.toFile()?.let {
            it.writeText(content); println("Written graph to ${it.absolutePath}")
        } ?: println(content)
    }

    private fun String.toBase64(): String {
        val escapedCode = this.replace("\"", "\\\"").replace("\n", "\\n")
        val data =
            "{\"code\":\"$escapedCode\",\"mermaid\": {\"theme\": \"default\"},\"updateEditor\":true,\"autoSync\":true,\"updateDiagram\":true}"
        return Base64.getEncoder().encodeToString(data.toByteArray()).trimEnd('=')
    }

    private fun download(url: String, outputPath: Path? = null): String {
        val outputFile = outputPath?.toFile() ?: File("image.png")
        URL(url).openStream().transferTo(outputFile.outputStream())
        return outputFile.absolutePath
    }
}