package ch.derlin.dc2mermaid

import ch.derlin.dc2mermaid.data.DockerCompose
import ch.derlin.dc2mermaid.graph.CONNECTOR.DOT_ARROW
import ch.derlin.dc2mermaid.graph.CONNECTOR.DOT_X
import ch.derlin.dc2mermaid.graph.MermaidGraph
import ch.derlin.dc2mermaid.graph.Shape.*
import ch.derlin.dc2mermaid.graph.withGeneratedIds
import ch.derlin.dc2mermaid.helpers.YamlUtils


fun generateMermaid(
    dockerComposeContent: String,
    withPorts: Boolean = false,
    withVolumes: Boolean = false,
    withExtendedLinks: Boolean = false,
    withClasses: Boolean = false,
    withScpClasses: Boolean = false
): String {

    val dc = DockerCompose(YamlUtils.load(dockerComposeContent))
    val graph = MermaidGraph()

    dc.services.forEach {
        graph.addNode(it.name, shape = if (it.name == "db") CYNLINDRIC else null)
    }

    val volumeIds = if (!withVolumes) listOf() else {
        dc.services.flatMap { it.volumes() }.withGeneratedIds("V") { id, volume ->
            graph.addNode(volume.target, id, HEXAGON)
            graph.addLink(id, volume.service, connector = DOT_X, text = volume.mounted)
        }
    }

    (if (withExtendedLinks) dc.extendedLinks else dc.links).sortedBy { it.from }.forEach { link ->
        graph.addLink(link.from, link.to, text = link.alias)
    }


    val portIds = if (!withPorts) listOf() else dc.ports.withGeneratedIds("P") { id, port ->
        graph.addNode(port.external, id, ROUND)
        graph.addLink(id, port.service, connector = DOT_ARROW, text = port.internalIfDifferent)
    }

    if (withClasses) {
        if (volumeIds.isNotEmpty())
            graph.addClass(
                "classDef volumes fill:#fdfae4,stroke:#867a22",
                "class " + volumeIds.joinToString(",") + " volumes"
            )

        if (portIds.isNotEmpty())
            graph.addClass(
                "classDef ports fill:#f8f8f8,stroke:#ccc",
                "class " + portIds.joinToString(",") + " ports"
            )

        if (withScpClasses)
            graph.addClass(
                "classDef comp fill:#fbfff7,stroke:#8bc34a",
                "class service,web,bff,db comp"
            )
    }

    return graph.build()
}