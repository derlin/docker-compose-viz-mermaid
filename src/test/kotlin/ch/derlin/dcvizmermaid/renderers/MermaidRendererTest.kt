package ch.derlin.dcvizmermaid.renderers

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import ch.derlin.dcvizmermaid.graph.GraphTheme
import dummyGraph
import isJpeg
import isSvg
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import size
import tmpFileWithExtension
import java.net.URL

class MermaidRendererTest {
    @Test
    fun `preview and editor link`() {
        val url = assertDoesNotThrow { MermaidRenderer.getPreviewLink(dummyGraph(), GraphTheme.DARK) }
        assertIsValidEditorLink(url)
    }

    @Test
    fun `editor link`() {
        val url = assertDoesNotThrow { MermaidRenderer.getEditorLink(dummyGraph(), GraphTheme.DARK) }
        assertIsValidEditorLink(url)
    }

    @Test
    fun `generate invalid graph`() {
        val outFile = tmpFileWithExtension(".png")
        val message =
            assertThrows<Exception> {
                MermaidRenderer.savePng(outFile, graph = "invalid graph", theme = GraphTheme.DEFAULT, bgColor = null)
            }.message
        assertThat(message).isNotNull().contains("response code", "400")
    }

    @Test
    fun `generate png (actually jpeg)`() {
        val outFile = tmpFileWithExtension(".jpeg")
        assertDoesNotThrow {
            MermaidRenderer.savePng(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isJpeg()).isTrue()
    }

    @Test
    fun `generate svg`() {
        val outFile = tmpFileWithExtension(".svg")
        assertDoesNotThrow {
            MermaidRenderer.saveSvg(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isSvg()).isTrue()
    }

    private fun assertIsValidEditorLink(url: String) {
        val content = assertDoesNotThrow { URL(url).readText() }
        assertThat(content).transform { it.lowercase() }.contains("<!doctype", "mermaid live editor")
    }
}
