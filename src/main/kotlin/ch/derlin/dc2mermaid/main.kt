package ch.derlin.dc2mermaid

import ch.derlin.dc2mermaid.data.DockerCompose
import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils
import java.io.File

fun main() {
    val dc = File("docker-compose.yaml")
        .readText()
        .let { YamlUtils.yaml.load<YAML>(it) }
        .let { DockerCompose(it) }


    val unreferenced = dc.services.map { it.name }.toMutableSet()

    val builder = StringBuilder()
    builder.appendLine("flowchart TB")

    var volumeIds = 0
    dc.services.forEach { service ->
        service.volumes().forEach {
            builder.appendLine("  V$volumeIds${"{{"} ${it.value} ${"}}"} -. ${it.key} .-x ${service.name}")
            unreferenced -= service.name
            volumeIds++
        }
    }
    builder.appendLine()

    dc.extendedLinks.sortedBy { it.from }.forEach { link ->
        val connector = link.alias?.let { "-- $it -->" } ?: "-->"
        builder.appendLine("  ${link.from} $connector ${link.to}")
        unreferenced -= setOf(link.from, link.to)
    }

    unreferenced.forEach { builder.appendLine("  $it") }
    builder.appendLine()

    var portIds = 0
    dc.ports.forEach { port ->
        val connector = if (port.external != port.internal) "-. ${port.internal} .->" else "-.->"
        builder.appendLine("  P$portIds((${port.external})) $connector ${port.service}")
        portIds++
    }
    builder.appendLine()

    if (volumeIds > 0) {
        builder.appendLine()
        builder.appendLine("classDef volumes fill:#fdfae4,stroke:#867a22")
        builder.appendLine("class " + (0..volumeIds).joinToString(",") { "V$it" } + " volumes")
    }

    if (portIds > 0) {
        builder.appendLine()
        builder.appendLine("classDef ports fill:#f8f8f8,stroke:#ccc")
        builder.appendLine("class " + (0..portIds).joinToString(",") { "P$it" } + " ports")
    }

    builder.appendLine()
    builder.appendLine("classDef comp fill:#fbfff7,stroke:#8bc34a")
    builder.appendLine("class service,web,bff,db comp")

    println(builder.toString())
}