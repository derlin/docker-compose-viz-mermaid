package ch.derlin.dcvizmermaid.renderers

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import ch.derlin.dcvizmermaid.graph.GraphTheme
import dummyGraph
import isPng
import isSvg
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import size
import tmpFileWithExtension

class KrokiRendererTest {

    @Test
    fun `generate png`() {
        val outFile = tmpFileWithExtension(".png")
        assertDoesNotThrow {
            KrokiRenderer.savePng(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isPng()).isTrue()
    }

    @Test
    fun `generate svg`() {
        val outFile = tmpFileWithExtension(".svg")
        assertDoesNotThrow {
            KrokiRenderer.saveSvg(outFile, graph = dummyGraph(), theme = GraphTheme.DEFAULT, bgColor = null)
        }
        assertThat(outFile.size()).isGreaterThan(0L)
        assertThat(outFile.isSvg()).isTrue()
    }
}
