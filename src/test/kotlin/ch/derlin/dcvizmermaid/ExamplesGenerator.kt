package ch.derlin.dcvizmermaid

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isSuccess
import ch.derlin.dcvizmermaid.graph.GraphTheme
import ch.derlin.dcvizmermaid.graph.MermaidOutput
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

class ExamplesGenerator {

    companion object {
        private val genDocs = System.getenv("GEN_DOCS") == "1"
        private val outputPathImages = if (genDocs) "docs/assets/generated" else "target/generated"
        private val outputPathText = if (genDocs) "docs/_includes/generated" else "target/generated"


        @BeforeAll
        @JvmStatic
        fun cleanup() {
            println("Writing to $outputPathImages and $outputPathText")
            listOf(outputPathImages, outputPathText).map { File(it) }.forEach {
                it.deleteRecursively()
                it.mkdirs()
            }
        }
    }

    @Test
    fun `generate help`() {
        assertThat {
            File("$outputPathText/help.md").writeText(Cli().getFormattedHelp())
        }.isSuccess()
    }

    @Test
    fun `generate examples`() {
        assertAll {
            File("src/test/resources/examples/full").walkTopDown()
                .filter { it.isFile && it.extension == "yaml" }
                .forEach { assertThat { processFullExamples(it) }.isSuccess() }

            File("src/test/resources/examples/partial").walkTopDown()
                .filter { it.isFile && it.extension == "yaml" }
                .forEach { assertThat { processYamlPartial(it) }.isSuccess() }
        }
    }

    private fun processFullExamples(file: File) {

        file.copyTo(File("$outputPathText/${file.name}"))

        val text = file.readText()
        val options = text.lines().first().substringAfter("#").takeIf { it.trim().all { c -> c.lowercase() in "pvc" } } ?: ""

        GraphTheme.values().forEach { theme ->
            val basename = "${file.nameWithoutExtension}-${theme.name.lowercase()}"
            val graph = generateMermaidGraph(
                text,
                theme = theme,
                withPorts = 'P' !in options,
                withVolumes = 'V' !in options,
                withClasses = 'C' !in options,
            )

            MermaidOutput.MARKDOWN.process(graph, Path.of("$outputPathText/$basename.md"), withBackground = false)
            MermaidOutput.SVG.process(graph, Path.of("$outputPathImages/$basename.svg"), withBackground = true)
            MermaidOutput.PNG.process(graph, Path.of("$outputPathImages/$basename.png"), withBackground = true)
        }
    }

    private fun processYamlPartial(file: File) {
        val text = file.readText()

        // only images
        GraphTheme.values().forEach { theme ->
            val basename = "${file.nameWithoutExtension}-${theme.name.lowercase()}"
            val graph = generateMermaidGraph(
                text, theme = theme,
                withPorts = true, withVolumes = true, withClasses = true, withImplicitLinks = true
            )

            MermaidOutput.SVG.process(graph, Path.of("$outputPathImages/$basename.svg"), withBackground = true)
        }
    }
}