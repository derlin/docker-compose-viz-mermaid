package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.data.DockerCompose
import ch.derlin.dcvizmermaid.graph.CONNECTOR.DOT_ARROW
import ch.derlin.dcvizmermaid.graph.CONNECTOR.DOT_DBL_X
import ch.derlin.dcvizmermaid.graph.CONNECTOR.DOT_LINE
import ch.derlin.dcvizmermaid.graph.CONNECTOR.DOT_X
import ch.derlin.dcvizmermaid.graph.GraphOrientation
import ch.derlin.dcvizmermaid.graph.GraphTheme
import ch.derlin.dcvizmermaid.graph.MermaidGraph
import ch.derlin.dcvizmermaid.graph.NetworksClazz
import ch.derlin.dcvizmermaid.graph.PortsClazz
import ch.derlin.dcvizmermaid.graph.ScpClazz
import ch.derlin.dcvizmermaid.graph.Shape.CIRCLE
import ch.derlin.dcvizmermaid.graph.Shape.CYLINDER
import ch.derlin.dcvizmermaid.graph.Shape.PARALLELOGRAM
import ch.derlin.dcvizmermaid.graph.VolumeClazz
import ch.derlin.dcvizmermaid.graph.toShape
import ch.derlin.dcvizmermaid.graph.toValidId
import ch.derlin.dcvizmermaid.graph.withGeneratedIds
import ch.derlin.dcvizmermaid.helpers.YamlUtils

object GenerateGraph {

    private const val ANONYMOUS_VOLUME_TEXT = "⋅ ∃ ⋅"
    private val knownDbs = listOf("db", "database", "redis", "mysql", "postgres", "postgresql", "mongo", "mongodb")

    @Suppress("LongParameterList")
    operator fun invoke(
        dockerComposeContent: String,
        direction: GraphOrientation = GraphOrientation.TB,
        theme: GraphTheme = GraphTheme.DEFAULT,
        withPorts: Boolean = false,
        withVolumes: Boolean = false,
        withNetworks: Boolean = false,
        withImplicitLinks: Boolean = false,
        withClasses: Boolean = false,
        withScpClasses: Boolean = false,
    ): MermaidGraph {

        val dc = DockerCompose(YamlUtils.load(dockerComposeContent))
        val graph = MermaidGraph(direction, theme)

        addServices(graph, dc)
        val volumeIds = addVolumes(graph, dc, withVolumes)
        addLinks(graph, dc, withImplicitLinks)
        val portIds = addPorts(graph, dc, withPorts)
        val netIds = addNetworks(graph, dc, withNetworks)

        if (withClasses) {
            if (volumeIds.isNotEmpty())
                graph.addClass(VolumeClazz, volumeIds)
            if (portIds.isNotEmpty())
                graph.addClass(PortsClazz, portIds)
            if (netIds.isNotEmpty())
                graph.addClass(NetworksClazz, netIds)
            if (withScpClasses)
                graph.addClass(ScpClazz, "service,web,bff,db".split(","))
        }

        return graph
    }

    private fun addServices(graph: MermaidGraph, dc: DockerCompose) =
        dc.services.forEach {
            graph.addNode(it.name, shape = if (it.name in knownDbs) CYLINDER else null)
        }

    private fun addVolumes(graph: MermaidGraph, dc: DockerCompose, withVolumes: Boolean): List<String> =
        if (!withVolumes) listOf() else {
            var num = 0
            dc.volumeBindings.map { volume ->
                val id = "V" + (volume.source ?: num++).toValidId()
                val text = if (volume.isAnonymousVolume()) ANONYMOUS_VOLUME_TEXT else volume.source ?: " "
                graph.addNode(text, id, volume.type.toShape())
                graph.addLink(id, volume.service, connector = if (volume.ro) DOT_X else DOT_DBL_X, text = volume.target)
                id
            }
        }

    private fun addLinks(graph: MermaidGraph, dc: DockerCompose, withImplicitLinks: Boolean) =
        (if (withImplicitLinks) dc.implicitLinks else dc.links).sortedBy { it.from }.forEach { link ->
            graph.addLink(link.from, link.to, text = link.alias)
        }

    private fun addPorts(graph: MermaidGraph, dc: DockerCompose, withPorts: Boolean): List<String> =
        if (!withPorts) listOf() else dc.ports.withGeneratedIds("P") { id, port ->
            graph.addNode(port.externalPort, id, CIRCLE)
            graph.addLink(id, port.service, connector = DOT_ARROW, text = port.internalIfDifferent)
        }

    private fun addNetworks(graph: MermaidGraph, dc: DockerCompose, withNetworks: Boolean): List<String> =
        if (!withNetworks) listOf() else dc.networkBindings.map { (name, bindings) ->
            val id = graph.addNode(name, shape = PARALLELOGRAM)
            bindings.forEach {
                graph.addLink(it.service, it.network, DOT_LINE, text = it.displayAlias())
            }
            id
        }
}
