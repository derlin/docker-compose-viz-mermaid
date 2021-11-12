package ch.derlin.dcvizmermaid.graph

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
            PNG, SVG -> MermaidRenderer(text, mermaidGraph.theme).let {
                (if (this == PNG) it.getPngLink() else it.getSvgLink()).downloadImage(outputFile, ext = this.name.lowercase())
            }
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
        println("Saved image to $outputFile")
        return outputFile.absolutePath
    }


}