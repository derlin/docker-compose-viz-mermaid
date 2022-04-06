package ch.derlin.dcvizmermaid.graph

import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.renderers.MermaidRenderer
import java.io.File
import java.nio.file.Path


enum class MermaidOutput {
    TEXT, MARKDOWN, EDITOR, PREVIEW, PNG, SVG;

    fun process(mermaidGraph: MermaidGraph, outputFile: Path? = null, withBackground: Boolean = false) {
        val text = buildGraph(mermaidGraph, withBackground)
        val renderer = Config.renderer
        val previewer = MermaidRenderer
        val bgColor = if (withBackground) mermaidGraph.theme.bgColor() else null

        when (this) {
            TEXT -> outputFile.print(text)
            MARKDOWN -> outputFile.print("```mermaid\n$text```")
            EDITOR -> println(previewer.getEditorLink(text, mermaidGraph.theme))
            PREVIEW -> println(previewer.getPreviewLink(text, mermaidGraph.theme))
            PNG -> renderer.savePng(outputFile.orDefaultForExtension("png"), text, mermaidGraph.theme, bgColor)
            SVG -> renderer.saveSvg(outputFile.orDefaultForExtension("svg"), text, mermaidGraph.theme, bgColor)
        }
    }

    private fun buildGraph(mermaidGraph: MermaidGraph, withBackground: Boolean) = when (this) {
        TEXT, MARKDOWN, EDITOR -> mermaidGraph.build(withBackground)
        else -> mermaidGraph.build() // mermaid.ink supports background color
    }

    private fun Path?.print(content: String) {
        this?.toFile()?.let {
            it.writeText(content); println("Written graph to ${it.absolutePath}")
        } ?: println(content)
    }

    private fun Path?.orDefaultForExtension(ext: String) = this?.toFile() ?: File("image.$ext")
}
