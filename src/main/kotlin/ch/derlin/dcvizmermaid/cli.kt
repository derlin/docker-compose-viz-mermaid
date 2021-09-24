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
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.nio.file.Path

class Cli : CliktCommand(
    """
    Generate a mermaid graph from a docker-compose file.
     
    There are different kind of outputs:
    ```
    * 'text' (default) outputs the mermaid graph (use -o to output to a file instead of stdout);
    * 'markdown' is same as text, but wraps the graph text in '```mermaid```'
    * 'png' or 'svg' generates the image and saves it 'image.[png|svg]' (use -o to change the destination);
    * 'editor' // 'preview' generates a link to the mermaid online editor, and print it to the console.
    
    When using theme and classes, the output may become hard to read depending on the background. 
    It is thus possible to force a background (using a hack) with the option `-b`.
    ```
""".trimIndent()
) {

    private val defaultFiles = listOf("docker-compose.yaml", "docker-compose.yml")

    private val classHelp = "If set, add some classes to mermaid to make the output nicer"
    private val outHelp = "Only available for format TEXT and PNG"
    private val linksHelp = "If set, try to find implicit links/depends_on by looking at the environment variables, " +
            "see if one if pointing to the host:port of another service"
    private val formatHelp = "Control the output format, case-insensitive."

    private val dockerComposeInput: File?
            by argument("docker-compose-path").file(mustExist = true, mustBeReadable = true, canBeDir = false).optional()

    private val direction: GraphOrientation
            by option("-d", "--dir", help = "Graph orientation").enum<GraphOrientation>(ignoreCase = true).default(GraphOrientation.TB)
    private val theme: GraphTheme
            by option("-t", "--theme", help = "Graph theme").enum<GraphTheme>(ignoreCase = true).default(GraphTheme.DEFAULT)
    private val withPorts: Boolean
            by option("--ports", "-p").flag("--no-ports", "-P", default = false)
    private val withVolumes: Boolean
            by option("--volumes", "-v").flag("--no-volumes", "-V", default = true)
    private val withImplicitLinks: Boolean
            by option("--ilinks", "-l", help = linksHelp).flag("--no-ilinks", "-L", default = true)
    private val withClasses: Boolean
            by option("--classes", "-c", help = classHelp).flag("--no-classes", "-C", default = true)
    private val withScpClasses: Boolean
            by option("--scpa", "-s", hidden = true).flag(default = false)

    private val outputType: MermaidOutput
            by option("--format", "-f", help = formatHelp).enum<MermaidOutput>(ignoreCase = true).default(MermaidOutput.TEXT)
    private val forceBackground: Boolean
            by option("--with-bg", "-b", help = linksHelp).flag("--no-bg", "-B", default = false)

    private val outputFile: Path? by option("--out", "-o", help = outHelp).path()

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true, maxWidth = 100) }
    }

    override fun run() {
        val mermaidGraph = generateMermaidGraph(
            (dockerComposeInput ?: findDefaultFile()).readText(),
            direction = direction,
            theme = theme,
            withPorts = withPorts,
            withVolumes = withVolumes,
            withImplicitLinks = withImplicitLinks,
            withClasses = withClasses,
            withScpClasses = withScpClasses
        )

        outputType.process(mermaidGraph, outputFile)
    }

    private fun findDefaultFile() =
        listOf("docker-compose.yaml", "docker-compose.yml")
            .map { File(it) }
            .firstOrNull { it.isFile && it.exists() }
            ?: throw UsageError(
                "Could not find docker-compose file looking for ${defaultFiles.joinToString(", ")}." +
                        "Please, provide the exact path as argument."
            )

}

fun main(args: Array<String>) = Cli().main(args)