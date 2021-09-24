package ch.derlin.dcvizmermaid.graph

import ch.derlin.dcvizmermaid.renderers.KrokiRenderer
import ch.derlin.dcvizmermaid.renderers.MermaidRenderer
import java.io.File
import java.net.URL
import java.nio.file.Path


enum class MermaidOutput {
    TEXT, MARKDOWN, EDITOR, PREVIEW, PNG, SVG;

    fun process(mermaidGraph: MermaidGraph, outputFile: Path? = null, withBackground: Boolean = false) {
        val text = mermaidGraph.build(withBackground)

        when (this) {
            TEXT -> outputFile.print(text)
            MARKDOWN -> outputFile.print("```mermaid\n$text```")
            EDITOR -> println(MermaidRenderer(text, mermaidGraph.theme).getEditorLink())
            PREVIEW -> println(MermaidRenderer(text, mermaidGraph.theme).getPreviewLink())
            PNG -> MermaidRenderer(text, mermaidGraph.theme).getPngLink().downloadImage(outputFile).let { println("Saved image to $it") }
            // use kroki! for now, as mermaid crops the texts on connectors and non-rectangle shapes in svg preview even more
            SVG -> KrokiRenderer.getSvgLink(text).downloadImage(outputFile, ext = "svg").let { println("Saved image to $it") }
        }
    }

    private fun Path?.print(content: String) {
        this?.toFile()?.let {
            it.writeText(content); println("Written graph to ${it.absolutePath}")
        } ?: println(content)
    }

    private fun String.downloadImage(outputPath: Path? = null, ext: String = "png"): String {
        val outputFile = outputPath?.toFile() ?: File("image.$ext")
        URL(this).openStream().transferTo(outputFile.outputStream())
        return outputFile.absolutePath
    }


}