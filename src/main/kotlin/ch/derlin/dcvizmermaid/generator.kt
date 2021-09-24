package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.data.DockerCompose
import ch.derlin.dcvizmermaid.graph.*
import ch.derlin.dcvizmermaid.graph.CONNECTOR.*
import ch.derlin.dcvizmermaid.graph.Shape.CIRCLE
import ch.derlin.dcvizmermaid.graph.Shape.CYLINDER
import ch.derlin.dcvizmermaid.helpers.YamlUtils

val knownDbs = listOf("db", "database", "redis", "mysql", "postgres", "postgresql", "mongo", "mongodb")

fun generateMermaidGraph(
    dockerComposeContent: String,
    direction: GraphOrientation = GraphOrientation.TB,
    theme: GraphTheme = GraphTheme.DEFAULT,
    withPorts: Boolean = false,
    withVolumes: Boolean = false,
    withImplicitLinks: Boolean = false,
    withClasses: Boolean = false,
    withScpClasses: Boolean = false,
): MermaidGraph {

    val dc = DockerCompose(YamlUtils.load(dockerComposeContent))
    val graph = MermaidGraph(direction, theme)

    dc.services.forEach {
        graph.addNode(it.name, shape = if (it.name in knownDbs) CYLINDER else null)
    }

    val volumeIds = if (!withVolumes) listOf() else {
        var num = 0
        dc.volumeBindings.map { volume ->
            val id = "V" + (volume.source ?: num++).toValidId()
            graph.addNode(volume.source ?: " ", id, volume.type.toShape())
            graph.addLink(id, volume.service, connector = if (volume.ro) DOT_X else DOT_DBL_X, text = volume.target)
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
            graph.addClass(VolumeClazz, volumeIds)
        if (portIds.isNotEmpty())
            graph.addClass(PortsClazz, portIds)
        if (withScpClasses)
            graph.addClass(ScpClazz, "service,web,bff,db scp".split(","))
    }

    return graph
}