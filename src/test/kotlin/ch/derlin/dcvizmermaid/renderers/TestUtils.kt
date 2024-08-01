import ch.derlin.dcvizmermaid.GenerateGraph
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

fun dummyGraph(): String =
    GenerateGraph(
        """
        services:
          web:
            image: derlin/rickroller:latest
            ports:
              - 8080:80
            volumes:
              - ./foo.conf:/data/.foo.conf
              - settings:/settings
        volumes:
           settings:
        """.trimIndent(),
        withPorts = true,
        withVolumes = true,
    ).build()

fun tmpFileWithExtension(extension: String) = File.createTempFile("test", extension).also { it.deleteOnExit() }

fun File.size(): Long = Files.readAttributes(this.toPath(), BasicFileAttributes::class.java).size()

fun File.isJpeg(): Boolean = startsWithBytes("FF", "D8", "FF")

fun File.isPng(): Boolean = startsWithBytes("89", "50", "4E", "47", "0D", "0A", "1A", "0A")

fun File.isSvg(): Boolean {
    if (!isFile || !exists()) return false
    val content = readText().trim()
    return content.startsWith("<svg") && content.endsWith("</svg>")
}

private fun File.startsWithBytes(vararg hexBytes: String): Boolean {
    if (!isFile || !exists()) return false

    val magicBytes = hexBytes.map { it.toInt(16).toByte() }
    val outBytes = ByteArray(magicBytes.size)
    inputStream().use {
        it.read(outBytes)
    }
    return magicBytes.zip(outBytes.toList()).all { (expected, actual) -> expected == actual }
}
