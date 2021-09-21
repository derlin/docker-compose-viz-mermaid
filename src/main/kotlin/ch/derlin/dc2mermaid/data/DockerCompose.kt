package ch.derlin.dc2mermaid.data

import ch.derlin.dc2mermaid.data.Service.Link
import ch.derlin.dc2mermaid.data.Service.MaybeReference
import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath


class DockerCompose(private val content: YAML) {
    // no version => version 1
    val isVersion1 = content.getOrDefault("version", 1) == 1

    val services: List<Service> by lazy {
        val root = if (isVersion1) content else content.getByPath("services", type = Map::class) as YAML
        root
            .filterNot { it.key == "version" }
            .filterNot { it.value == null }
            .map { Service(it.key, it.value as YAML) }
    }

    val links: Set<Link> by lazy { services.flatMap { it.links() }.toSet() }
    val ports: Set<PortBinding> by lazy { services.flatMap { it.ports() }.toSet() }

    val extendedLinks: Set<Link> by lazy {
        this.links + services.flatMap { linksFromMaybeRefs(it.name, this.ports, it.envMatchingPorts()) }
    }

    val volumeBindings: Set<VolumeBinding> by lazy {
        processVolumes(volumes(), services.flatMap { it.volumes() }).toSet()
    }

    private fun volumes(): Map<String, String?> = if (!content.containsKey("volumes")) mapOf() else {
        (content["volumes"] as YAML).mapValues { it.value as String? }
    }

    companion object {
        fun linksFromMaybeRefs(serviceName: String, ports: Iterable<PortBinding>, maybeRefs: Iterable<MaybeReference>): List<Link> =
            maybeRefs.mapNotNull { maybeRef ->
                val maybePort = if (maybeRef.internal) {
                    ports.find { it.service == maybeRef.service && it.internalPort == maybeRef.port }
                } else {
                    ports.find { it.externalPort == maybeRef.port }
                }
                maybePort?.let { Link(serviceName, it.service) }
            }

        fun processVolumes(globalVolumes: Map<String, String?>, volumeBindings: Collection<VolumeBinding>) =
            volumeBindings.map { binding ->
                if (binding.target !in globalVolumes) binding
                else binding.copy(source = globalVolumes[binding.target], inline = false)
            }
    }

}
