package ch.derlin.dcvizmermaid.renderers

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import ch.derlin.dcvizmermaid.graph.GraphTheme
import dummyGraph
import isSvg
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import size
import tmpFileWithExtension

class KrokiRendererTest {

    // PNG is not implemented

    @Test
    fun `generate svg`() {
        val outFile = tmpFileWithExtension(".png")
        val outFile = tmpFileWithExtension(".svg")
        assertDoesNotThrow {
            MermaidRenderer.saveSvg(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
            KrokiRenderer.saveSvg(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isSvg()).isTrue()
    }
}
