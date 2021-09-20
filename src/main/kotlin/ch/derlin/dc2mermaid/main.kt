package ch.derlin.dc2mermaid

import ch.derlin.dc2mermaid.data.DockerCompose
import ch.derlin.dc2mermaid.graph.CONNECTOR.*
import ch.derlin.dc2mermaid.graph.MermaidGraph
import ch.derlin.dc2mermaid.graph.Shape.*
import ch.derlin.dc2mermaid.graph.withGeneratedIds
import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils
import java.io.File


fun main() {
    val dc = File("docker-compose.yaml")
        .readText()
        .let { YamlUtils.yaml.load<YAML>(it) }
        .let { DockerCompose(it) }

    val graph = MermaidGraph()

    dc.services.forEach {
        graph.addNode(it.name, shape = if (it.name == "db") CYNLINDRIC else null)
    }

    val volumeIds = dc.services.flatMap { it.volumes() }.withGeneratedIds("V") { id, volume ->
        graph.addNode(volume.target, id, HEXAGON)
        graph.addLink(id, volume.service, connector = DOT_X, text = volume.mounted)
    }

    dc.links.sortedBy { it.from }.forEach { link ->
        graph.addLink(link.from, link.to, text = link.alias)
    }

    val portIds = dc.ports.withGeneratedIds("P") { id, port ->
        graph.addNode(port.external, id, ROUND)
        graph.addLink(id, port.service, connector = DOT_ARROW, text = port.internalIfDifferent)
    }

    if (volumeIds.isNotEmpty()) {
        graph.addClass(
            "classDef volumes fill:#fdfae4,stroke:#867a22",
            "class " + volumeIds.joinToString(",") + " volumes"
        )
    }

    if (portIds.isNotEmpty()) {
        graph.addClass(
            "classDef ports fill:#f8f8f8,stroke:#ccc",
            "class " + portIds.joinToString(",") + " ports"
        )
    }

    graph.addClass(
        "classDef comp fill:#fbfff7,stroke:#8bc34a",
        "class service,web,bff,db comp"
    )

    println(graph.build())
}