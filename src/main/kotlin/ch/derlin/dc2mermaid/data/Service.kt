package ch.derlin.dc2mermaid.data

import ch.derlin.dc2mermaid.helpers.YAML
import ch.derlin.dc2mermaid.helpers.YamlUtils.getByPath
import ch.derlin.dc2mermaid.helpers.YamlUtils.getListByPath

class Service(val name: String, private val content: YAML) {

    fun links() = linksFromLinks() + linksFromDependsOn()

    private fun getAllLinks(): List<String> =
        content.getListByPath("depends_on", listOf<String>()) +
                content.getListByPath("links", listOf<String>())

    private fun linksFromLinks(): List<Link> =
        content.getListByPath("links", listOf<String>())
            .map { link ->
                val split = link.split(":")
                Link(name, split[0], if (split.size > 1) split[1] else null)
            }

    private fun linksFromDependsOn(): List<Link> =
        content.getListByPath("depends_on", listOf<String>())
            .map { Link(name, it) }

    fun volumes(): Map<String, String> =
        content.getListByPath("volumes", listOf<String>())
            .map { it.split(":") }
            .filter { it.size >= 2 }
            .associateTo(mutableMapOf()) { it[0] to it[1] }

    fun ports(): List<PortBinding> =
        content.getListByPath("ports", listOf<Any>())
            .mapNotNull { PortBinding.parse(name, it) }

    fun envMatchingPorts(): List<MaybeReference> =
        content.getByPath("environment")
            ?.let { it as? YAML }?.values?.mapNotNull { it as? String }
            ?.filter { ":" in it }
            ?.mapNotNull { value ->
                "^(?:https?://)?([^:]+):(\\d+)[ ;,]*$".toRegex().matchEntire(value)?.let {
                    MaybeReference(
                        internal = it.groupValues[0] in listOf("localhost", "120.0.0.1", "${"{"}DOCKER_HOST_IP${"}"}"),
                        port = it.groupValues[2].toInt(),
                        service = it.groupValues[1]
                    )
                }
            } ?: listOf()

    override fun toString(): String = "Service[$name]"


    data class MaybeReference(val internal: Boolean, val port: Int, val service: String? = null)

    data class Link(val from: String, val to: String, val alias: String? = null)
}