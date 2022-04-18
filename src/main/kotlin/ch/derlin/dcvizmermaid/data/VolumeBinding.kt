package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.helpers.YAML
import ch.derlin.dcvizmermaid.helpers.YamlUtils.asYaml
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getByPath
import mu.KotlinLogging

data class VolumeBinding(
    val service: String,
    val source: String? = null,
    val target: String? = null,
    val type: VolumeType = if (source == null) VolumeType.VOLUME else VolumeType.BIND,
    val ro: Boolean = false,
) {
    init {
        requireNotNull(target ?: source) { "A volume binding should at least have a source or target defined" }
    }

    fun isAnonymousVolume() = type == VolumeType.VOLUME && source == null

    enum class VolumeType {
        BIND, // bind mount (the most common): linked to a path on the host
        VOLUME, // named volume: persistent, but created/stored under /var/lib/docker/volumes/ (anonymous volumes are possible)
        NPIPE, // for e.g. mounting docker.sock
        TMPFS // temporary directory a container can write to (!no source!)
    }

    companion object {

        private val logger = KotlinLogging.logger {}

        fun parse(service: String, volumeMapping: Any): VolumeBinding? = when (volumeMapping) {
            is String -> parseString(service, volumeMapping)
            is Map<*, *> -> parseYaml(service, volumeMapping.asYaml())
            else -> null
        }

        @Suppress("MagicNumber")
        private fun parseString(service: String, volumeMapping: String): VolumeBinding? =
            volumeMapping.split(":").let {
                when (it.size) {
                    1 ->
                        VolumeBinding(service, target = it[0], type = VolumeType.VOLUME) // anonymous volume
                    2 ->
                        if (it.last() in listOf("ro", "rw")) VolumeBinding(service, target = it[0], ro = it[1] == "ro")
                        else VolumeBinding(service, source = it[0], target = it[1])
                    3 ->
                        VolumeBinding(service, source = it[0], target = it[1], ro = it[2] == "ro")
                    else -> {
                        logger.warn { "Invalid volume mapping found: '$it' (more than 3 parts separated by :)" }
                        null
                    }
                }
            }

        @Suppress("TooGenericExceptionCaught") // too many things can happen here in case of invalid binding
        private fun parseYaml(service: String, volumeMapping: YAML) =
            volumeMapping.getByPath("target", String::class)?.let { target ->
                try {
                    VolumeBinding(
                        service = service,
                        target = target,
                        source = volumeMapping.getByPath("source", String::class),
                        type = VolumeType.valueOf((volumeMapping.getByPath("type", String::class) ?: "bind").uppercase()),
                        ro = volumeMapping.getByPath("read_only", Boolean::class) == true
                    )
                } catch (ex: Exception) {
                    logger.error("Could not read volume '$volumeMapping'") { ex }
                    null
                }
            }
    }
}
