package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.data.DockerCompose
import ch.derlin.dcvizmermaid.data.VolumeBinding.VolumeType
import ch.derlin.dcvizmermaid.graph.CONNECTOR.*
import ch.derlin.dcvizmermaid.graph.GraphOrientation
import ch.derlin.dcvizmermaid.graph.MermaidGraph
import ch.derlin.dcvizmermaid.graph.Shape.*
import ch.derlin.dcvizmermaid.graph.idGenerator
import ch.derlin.dcvizmermaid.graph.withGeneratedIds
import ch.derlin.dcvizmermaid.helpers.YamlUtils

val knownDbs = listOf("db", "redis", "mysql", "postgres", "postgresql")

fun generateMermaid(
    dockerComposeContent: String,
    direction: GraphOrientation = GraphOrientation.TB,
    withPorts: Boolean = false,
    withVolumes: Boolean = false,
    withImplicitLinks: Boolean = false,
    withClasses: Boolean = false,
    withScpClasses: Boolean = false
): String {

    val dc = DockerCompose(YamlUtils.load(dockerComposeContent))
    val graph = MermaidGraph()

    dc.services.forEach {
        graph.addNode(it.name, shape = if (it.name in knownDbs) CYLINDER else null)
    }

    val volumeIds = if (!withVolumes) listOf() else {
        val idGen = idGenerator("V")
        dc.volumeBindings.map { volume ->
            val id = if (volume.inline) idGen() else "V" + requireNotNull(volume.target)
            val details = if (volume.type == VolumeType.VOLUME) "" else "(${volume.type})"
            graph.addNode(volume.target + details, id, if (volume.inline) HEXAGON else RECT_ROUNDED)
            graph.addLink(id, volume.service, connector = if (volume.ro) DOT_X else DOT_DBL_X, text = volume.source)
            id
        }

    }

    (if (withImplicitLinks) dc.extendedLinks else dc.links).sortedBy { it.from }.forEach { link ->
        graph.addLink(link.from, link.to, text = link.alias)
    }


    val portIds = if (!withPorts) listOf() else dc.ports.withGeneratedIds("P") { id, port ->
        graph.addNode(port.externalPort, id, CIRCLE)
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