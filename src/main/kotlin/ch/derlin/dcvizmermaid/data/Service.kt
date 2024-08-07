package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.helpers.OrList
import ch.derlin.dcvizmermaid.helpers.OrMap
import ch.derlin.dcvizmermaid.helpers.YAML
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getListByPath
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getListOrMapByPath

class Service(val name: String, private val content: YAML) {
    fun links() = linksFromLinks() + linksFromDependsOn()

    private fun linksFromLinks(): List<Link> =
        content.getListByPath("links", listOf<String>())
            .map { Link.parse(name, it) }

    private fun linksFromDependsOn(): List<Link> =
        when (val dependsOn = content.getListOrMapByPath("depends_on")) {
            is OrList -> dependsOn.list
            is OrMap -> dependsOn.map.keys
            else -> listOf()
        }.map { Link.parse(name, it) }

    fun volumes(): List<VolumeBinding> =
        listOf("volumes", "volumes_from")
            .flatMap { content.getListByPath(it, listOf<Any>()) }
            .mapNotNull { VolumeBinding.parse(name, it) }

    fun ports(): List<PortBinding> =
        content.getListByPath("ports", listOf<Any>())
            .mapNotNull { PortBinding.parse(name, it) }

    fun networks(): List<NetworkBinding> = content.getListOrMapByPath("networks").let { NetworkBinding.parse(name, it) }

    fun envMatchingPorts(): List<MaybeReference> {
        // environments can be expressed either as list, or as map...
        val environmentValues =
            when (val env = content.getListOrMapByPath("environment")) {
                is OrList -> env.list.map { it.substringAfter("=") }
                is OrMap -> env.map.values.mapNotNull { it as? String }
                else -> listOf()
            }
        return environmentValues
            .filter { ":" in it }
            .mapNotNull { MaybeReference.parse(it) }
    }

    override fun toString(): String = "Service[$name]"

    data class MaybeReference(val internal: Boolean, val port: Int, val service: String? = null) {
        companion object {
            fun parse(s: String): MaybeReference? =
                "^(?:(?:[a-z]+:)+//)?([^:]+):(\\d+)(?:/.*)?$".toRegex().matchEntire(s)?.let {
                    MaybeReference(
                        internal = it.groupValues[1] !in listOf("localhost", "127.0.0.1", "$${"{"}DOCKER_HOST_IP${"}"}"),
                        port = it.groupValues[2].toInt(),
                        service = it.groupValues[1],
                    )
                }
        }
    }

    data class Link(val from: String, val to: String, val alias: String? = null) {
        companion object {
            fun parse(
                name: String,
                linkMapping: String,
            ): Link =
                linkMapping.split(":").let {
                    Link(name, it[0], if (it.size > 1) it[1] else null)
                }
        }
    }
}
