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

    val services = dc.fetchServices()
    val links = services.flatMap { it.links() }.toMutableSet()
    val ports = services.flatMap { it.ports() }

    services.forEach { service ->
        service.envMatchingPorts().forEach { maybeRef ->
            val port = if (maybeRef.internal) {
                ports.find { it.service == maybeRef.service && it.internal == maybeRef.port }
            } else {
                ports.find { it.external == maybeRef.port }
            }
            port?.let { links += (service.name to it.service) }
        }
    }

    val unreferenced = services.map { it.name }.toMutableSet()

    val builder = StringBuilder()
    builder.appendLine("flowchart TB")

    var volumeIds = 0
    services.forEach { service ->
        service.volumes().forEach {
            builder.appendLine("  V$volumeIds${"{{"} ${it.value} ${"}}"} -. ${it.key} .-x ${service.name}")
            unreferenced -= service.name
            volumeIds++
        }
    }
    builder.appendLine()

    links.sortedBy { it.first }.forEach { (from, to) ->
        builder.appendLine("  $from --> $to")
        unreferenced -= setOf(from, to)
    }
    unreferenced.forEach {
        builder.appendLine("  $it")
    }

    if (volumeIds > 0) {
        builder.appendLine()
        builder.appendLine("classDef volumes fill:#fdfae4,stroke:#867a22")
        builder.appendLine("class " + (0..volumeIds).joinToString(",") { "V$it" } + " volumes")
    }

    builder.appendLine()
    builder.appendLine("classDef comp fill:#fbfff7,stroke:#8bc34a")
    builder.appendLine("class service,web,bff,db comp")

    println(builder.toString())
}