package ch.derlin.dc2mermaid.data

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

    val links by lazy { services.flatMap { it.links() } }
    val ports by lazy { services.flatMap { it.ports() } }

    val extendedLinks by lazy {
        val links = this.links.toMutableSet()
        services.forEach { service ->
            service.envMatchingPorts().forEach { maybeRef ->
                val port = if (maybeRef.internal) {
                    ports.find { it.service == maybeRef.service && it.internal == maybeRef.port }
                } else {
                    ports.find { it.external == maybeRef.port }
                }
                port?.let { links += Service.Link(service.name, it.service) }
            }
        }
        links.toSet()
    }
}
