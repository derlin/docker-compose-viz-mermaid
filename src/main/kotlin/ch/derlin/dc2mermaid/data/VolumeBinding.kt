package ch.derlin.dc2mermaid.data;

import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath


data class VolumeBinding(
    val service: String,
    val source: String? = null,
    val target: String? = null,
    val type: VolumeType = VolumeType.VOLUME,
    val inline: Boolean = true,
    val ro: Boolean = false,
) {

    init {
        requireNotNull(target ?: source) { "A volume binding should at least have a source or target defined" }
    }

    enum class VolumeType { VOLUME, BIND }

    companion object {
        fun parse(service: String, volumeMapping: Any): VolumeBinding? = when (volumeMapping) {
            is String -> parseString(service, volumeMapping)
            is Map<*, *> -> parseYaml(service, volumeMapping as YAML)
            else -> null
        }

        private fun parseString(service: String, volumeMapping: String): VolumeBinding? =
            volumeMapping.split(":").let {
                when (it.size) {
                    1 ->
                        VolumeBinding(service, target = it[0])
                    2 ->
                        if (it.last() in listOf("ro", "rw")) VolumeBinding(service, target = it[0], ro = it[1] == "ro")
                        else VolumeBinding(service, source = it[0], target = it[1])
                    3 ->
                        VolumeBinding(service, source = it[0], target = it[1], ro = it[2] == "ro")
                    else -> null
                }
            }

        private fun parseYaml(service: String, volumeMapping: YAML) =
            volumeMapping.getByPath("target", String::class)?.let { target ->
                try {
                    VolumeBinding(
                        service = service,
                        target = target,
                        source = volumeMapping.getByPath("source", String::class),
                        type = VolumeType.valueOf((volumeMapping.getByPath("type", String::class) ?: "volume").uppercase()),
                        ro = volumeMapping.getByPath("read_only", Boolean::class) == true
                    )
                } catch (ex: Exception) {
                    println("Could not read volume $volumeMapping")
                    null
                }
            }
    }

}
