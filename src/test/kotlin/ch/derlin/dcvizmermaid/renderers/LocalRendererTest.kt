package ch.derlin.dcvizmermaid.renderers

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import ch.derlin.dcvizmermaid.graph.GraphTheme
import dummyGraph
import isJpeg
import isSvg
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import size
import tmpFileWithExtension

class LocalRendererTest {
    @Test
    fun `generate svg`() {
        val outFile = tmpFileWithExtension(".svg")
        assertDoesNotThrow {
            LocalRenderer.saveSvg(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isSvg()).isTrue()
    }

    @Test
    fun `generate png (actually jpeg)`() {
        val outFile = tmpFileWithExtension(".jpeg")
        assertDoesNotThrow {
            LocalRenderer.savePng(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isJpeg()).isTrue()
    }
}
