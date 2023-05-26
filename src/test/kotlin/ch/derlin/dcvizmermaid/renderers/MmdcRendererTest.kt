package ch.derlin.dcvizmermaid.renderers

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.exists
import assertk.assertions.isEmpty
import assertk.assertions.isNotNull
import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.graph.GraphTheme
import dummyGraph
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tmpFileWithExtension
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files

class MmdcRendererTest {

    @Test
    fun `install mmdc`() {
        val tempInstallDir = Files.createTempDirectory("tmpDirPrefix").toFile()
        tempInstallDir.deleteOnExit()
        System.setProperty("MMDC_INSTALL_PATH", tempInstallDir.absolutePath)
        val out = ByteArrayOutputStream()

        assertDoesNotThrow {
            MmdcRenderer.checkSystem(
                mmdcInstallPath = tempInstallDir.path,
                mmdcExecutable = Config.pathJoin(tempInstallDir.path, "node_modules", ".bin", "mmdc"),
                systemOut = PrintStream(out),
                systemIn = "y".byteInputStream()
            )
        }

        assertThat(out.toString()).contains("mermaid-cli installed")
    }

    @Test
    fun `abort mmdc installation`() {
        val tempInstallDir = Files.createTempDirectory("tmpDirPrefix").toFile()
        tempInstallDir.deleteOnExit()

        listOf("", "n", "yes").forEach {
            val ex = assertThrows<Exception> {
                MmdcRenderer.checkSystem(
                    mmdcInstallPath = tempInstallDir.path,
                    mmdcExecutable = Config.pathJoin(tempInstallDir.path, "node_modules", ".bin", "mmdc"),
                    systemIn = it.byteInputStream()
                )
            }
            assertThat(ex.message).isNotNull().contains("Aborting")
            assertThat(tempInstallDir.listFiles()).isNotNull().isEmpty()
        }
    }

    @Test
    fun `generate images`() {
        val graph = dummyGraph()

        assertAll {
            listOf(".png", ".svg").forEach { extension ->
                val outFile = tmpFileWithExtension(extension)
                val pngPath = assertDoesNotThrow {
                    // we use the same function here, since they both do the same
                    MmdcRenderer.savePng(outputFile = outFile, graph = graph, theme = GraphTheme.DARK, bgColor = null)
                }
                assertThat(File(pngPath)).exists()
            }
        }
    }
}
