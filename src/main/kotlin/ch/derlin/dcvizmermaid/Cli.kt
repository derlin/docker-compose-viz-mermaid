package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.graph.GraphOrientation
import ch.derlin.dcvizmermaid.graph.GraphTheme
import ch.derlin.dcvizmermaid.graph.MermaidOutput
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.nio.file.Path
import java.util.Properties
import kotlin.system.exitProcess

private const val GIT_PROPERTIES_FILE = "info.properties"

class Cli : CliktCommand(
    """
    Visualize a docker-compose, by converting it to a Mermaid graph.

    Supported outputs (`-f`):
    ```
    * 'text' (default) outputs the mermaid graph (use -o to output to a file instead of stdout);
    * 'markdown' is same as text, but wraps the graph text in '```mermaid```'
    * 'png' or 'svg' generates the image and saves it 'image.[png|svg]' (use -o to change the destination);
    * 'editor' // 'preview' generates a link to the mermaid online editor, and print it to the console.
    ```
    You can further customize the result using the options below.
    """.trimIndent()
) {

    private val defaultFiles = listOf("docker-compose.yaml", "docker-compose.yml")

    // NOTE: this awful formatting is the best I could do with ktlint

    private val dockerComposeInput: File? by
    argument("docker-compose-path").file(mustExist = true, mustBeReadable = true, canBeDir = false).optional()

    private val showVersionAndExit: Boolean by
    option("--version", help = "Show the version and exit").flag(default = false)

    class ProcessingOptions : OptionGroup(name = "Processing options") {
        private val linksHelp = "Try to find implicit links between services by looking at the environment variables"

        val withPorts: Boolean by
        option("--ports", "-p", help = "Extract and display ports").flag("--no-ports", "-P", default = false)
        val withVolumes: Boolean by
        option("--volumes", "-v", help = "Extract and display volumes").flag("--no-volumes", "-V", default = true)
        val withNetworks: Boolean by
        option("--networks", "-n", help = "Extract and display networks").flag("--no-networks", "-N", default = true)
        val withImplicitLinks: Boolean by
        option("--ilinks", "-l", help = linksHelp).flag("--no-ilinks", "-L", default = true)
    }

    private val processing by ProcessingOptions()

    class OutputOptions : OptionGroup(name = "Output options") {
        private val classHelp = "Add CSS classes to mermaid to make the output nicer"

        val type: MermaidOutput by
        option("--format", "-f", help = "Output type (case insensitive)").enum<MermaidOutput>(ignoreCase = true).default(MermaidOutput.TEXT)
        val file: Path? by
        option("--out", "-o", help = "Write output to a specific file").path()
        val forceBackground: Boolean by
        option("--with-bg", "-b", help = "Force background color").flag(default = false)
        val direction: GraphOrientation by
        option("--dir", "-d", help = "Graph orientation").enum<GraphOrientation>(ignoreCase = true).default(GraphOrientation.TB)
        val theme: GraphTheme by
        option("--theme", "-t", help = "Graph theme").enum<GraphTheme>(ignoreCase = true).default(GraphTheme.DEFAULT)
        val withClasses: Boolean by
        option("--classes", "-c", help = classHelp).flag("--no-classes", "-C", default = true)
        val withScpClasses: Boolean by
        option("--scp", "-s", hidden = true).flag(default = false)
    }

    private val output by OutputOptions()

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true, maxWidth = 100) }
    }

    override fun run() {
        if (showVersionAndExit) showVersionAndExit()

        val mermaidGraph = GenerateGraph(
            (dockerComposeInput ?: findDefaultFile()).readText(),
            direction = output.direction,
            theme = output.theme,
            withPorts = processing.withPorts,
            withVolumes = processing.withVolumes,
            withNetworks = processing.withNetworks,
            withImplicitLinks = processing.withImplicitLinks,
            withClasses = output.withClasses,
            withScpClasses = output.withScpClasses
        )

        output.type.process(mermaidGraph, output.file, withBackground = output.forceBackground)
    }

    private fun findDefaultFile() =
        listOf("docker-compose.yaml", "docker-compose.yml")
            .map { File(it) }
            .firstOrNull { it.isFile && it.exists() }
            ?: throw UsageError(
                "Could not find docker-compose file looking for ${defaultFiles.joinToString(", ")}. " +
                    "Please, provide the exact path as argument."
            )

    private fun showVersionAndExit() {
        with(Properties()) {
            // info.properties is generated on build by gradle (see gradle-git-properties plugin)
            load(Cli::class.java.classLoader.getResourceAsStream(GIT_PROPERTIES_FILE))
            println("Version: ${getProperty("git.build.version")} (sha: ${getProperty("git.commit.id")})")
            println()
            forEach { k, v -> println("$k=$v") }
        }
        exitProcess(0)
    }
}
