package ch.derlin.dcvizmermaid.renderers

import ch.derlin.dcvizmermaid.Config
import ch.derlin.dcvizmermaid.graph.GraphTheme
import mu.KotlinLogging
import java.io.File
import java.io.InputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit

object MmdcRenderer : Renderer {

    private val logger = KotlinLogging.logger {}

    private const val EXEC_TIMEOUT_SECONDS = 60L

    override fun savePng(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String =
        render(outputFile, graph, bgColor)

    override fun saveSvg(outputFile: File, graph: String, theme: GraphTheme, bgColor: String?): String =
        render(outputFile, graph, bgColor)

    private fun render(outputFile: File, graph: String, bgColor: String?): String {
        checkSystem(Config.mmdcLocalInstallPath, Config.mmdcExecutable)

        val tempFile = File.createTempFile("dcviz", "png")
        tempFile.bufferedWriter().use { it.write(graph) }
        tempFile.deleteOnExit()
        val command = "${Config.mmdcExecutable} -i ${tempFile.absolutePath} -o $outputFile ${bgColor?.let { "-b $it" } ?: ""}"
        logger.debug { "Invoking mermaid-cli with: $command" }
        Runtime.getRuntime().exec(command).let { process ->
            process.waitFor(EXEC_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            if (process.exitValue() != 0) error(process.errorStream.reader().readText())
        }
        return outputFile.absolutePath.toString()
    }

    internal fun checkSystem(
        mmdcInstallPath: String,
        mmdcExecutable: String,
        systemOut: PrintStream = System.out,
        systemIn: InputStream = System.`in`,
    ) {

        File(mmdcExecutable).let { f ->
            if (f.exists() && f.isFile && f.canExecute())
                return
        }

        val runtime = Runtime.getRuntime()
        if (runtime.exec("npm --version").waitFor() != 0) {
            error("NPM is not installed on your system. Please, install it or use another renderer.")
        }

        systemOut.print("Missing mermaid-cli. Will install it to $mmdcInstallPath. OK? [y|N] ")
        systemIn.reader().use {
            if (it.readText().trim() != "y") error("You did not confirm. Aborting.")
        }

        val installCommand = "npm install --prefix $mmdcInstallPath ${Config.mmdcVersion}"
        systemOut.println("Running: $installCommand")
        runtime.exec(installCommand).let { process ->
            if (process.waitFor() != 0) {

                error("Something went wrong while running: $installCommand: ${process.errorStream.reader().readText()}")
            }
        }
        systemOut.println("mermaid-cli installed!")
    }
}
