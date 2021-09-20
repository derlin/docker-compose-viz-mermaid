package ch.derlin.dc2mermaid.data

import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath


class DockerCompose(private val config: YAML) {
    // no version => version 1
    val isVersion1 = config.getOrDefault("version", 1) == 1

    fun fetchServices(): List<Service> {
        val root = if (isVersion1) config else config.getByPath("services", type = Map::class) as YAML
        return root
            .filterNot { it.key == "version" }
            .filterNot { it.value == null }
            .map { Service(it.key, it.value as YAML) }
    }
}
