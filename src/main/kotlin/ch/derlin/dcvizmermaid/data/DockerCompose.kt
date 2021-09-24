package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.data.Service.Link
import ch.derlin.dcvizmermaid.data.Service.MaybeReference
import ch.derlin.dcvizmermaid.helpers.YAML
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getByPath


class DockerCompose(private val content: YAML) {
    val services: List<Service> by lazy {
        val root = if ("services" !in content) content else content.getByPath("services", type = Map::class) as YAML
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
        processVolumes(namedVolumes(), services.flatMap { it.volumes() }).toSet()
    }

    private fun namedVolumes(): Set<String> = if (!content.containsKey("volumes")) setOf() else (content["volumes"] as YAML).keys


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

        fun processVolumes(namedVolumes: Collection<String>, volumeBindings: Collection<VolumeBinding>) =
            volumeBindings.map { binding ->
                if (binding.source !in namedVolumes) binding
                else binding.copy(type = VolumeBinding.VolumeType.VOLUME)
            }
    }

}
