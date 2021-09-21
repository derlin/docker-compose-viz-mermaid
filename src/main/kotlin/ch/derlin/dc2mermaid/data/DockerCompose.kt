package ch.derlin.dc2mermaid.data

import ch.derlin.dc2mermaid.data.Service.Link
import ch.derlin.dc2mermaid.data.Service.MaybeReference
import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath


class DockerCompose(private val config: YAML) {
    // no version => version 1
    val isVersion1 = config.getOrDefault("version", 1) == 1

    val services: List<Service> by lazy {
        val root = if (isVersion1) config else config.getByPath("services", type = Map::class) as YAML
        root
            .filterNot { it.key == "version" }
            .filterNot { it.value == null }
            .map { Service(it.key, it.value as YAML) }
    }

    val links: Set<Link> by lazy { services.flatMap { it.links() }.toSet() }
    val ports: Set<PortBinding> by lazy { services.flatMap { it.ports() }.toSet() }

    val extendedLinks: Set<Link> by lazy {
        this.links + services.flatMap { linksFromMaybeRefs(it.name, it.ports(), it.envMatchingPorts()) }
    }

    companion object {
        fun linksFromMaybeRefs(serviceName: String, ports: List<PortBinding>, maybeRefs: List<MaybeReference>): List<Link> =
            maybeRefs.mapNotNull { maybeRef ->
                val maybePort = if (maybeRef.internal) {
                    ports.find { it.service == maybeRef.service && it.internal == maybeRef.port }
                } else {
                    ports.find { it.external == maybeRef.port }
                }
                maybePort?.let { Link(serviceName, it.service) }
            }
    }

}
