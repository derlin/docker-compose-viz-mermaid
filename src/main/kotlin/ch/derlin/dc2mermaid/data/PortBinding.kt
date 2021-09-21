package ch.derlin.dc2mermaid.data

import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath

data class PortBinding(val service: String, val internalPort: Int, val externalPort: Int = internalPort) {

    val internalIfDifferent = if (internalPort == externalPort) null else internalPort

    companion object {
        fun parse(service: String, declaration: Any): PortBinding? =
            when (declaration) {
                is String -> parseString(service, declaration)
                is Map<*, *> -> parseYaml(service, declaration as YAML)
                else -> null
            }


        private fun parseString(service: String, declaration: String): PortBinding? {
            // TODO: ranges and bounds
            try {
                val split = declaration.substringBefore("/").split(":").reversed()
                val internal = split[0]
                val external = split.getOrNull(1)?.ifBlank { null } ?: internal
                return PortBinding(service, internal.toInt(), external.toInt())
            } catch (ex: NumberFormatException) {
                return null
            }
        }

        private fun parseYaml(service: String, declaration: YAML): PortBinding? {
            if (declaration.containsKey("published")) {
                declaration.getByPath("published", Int::class)?.let { external ->
                    return PortBinding(service, internalPort = declaration.getByPath("target", Int::class) ?: 0, externalPort = external)
                }
            }
            return null
        }
    }
}