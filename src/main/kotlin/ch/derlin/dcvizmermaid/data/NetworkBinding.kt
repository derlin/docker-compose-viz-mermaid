package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.helpers.ListOrMap
import ch.derlin.dcvizmermaid.helpers.OrList
import ch.derlin.dcvizmermaid.helpers.OrMap
import ch.derlin.dcvizmermaid.helpers.YAML
import ch.derlin.dcvizmermaid.helpers.YamlUtils.asYaml
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getListByPath

data class NetworkBinding(
    val network: String,
    val service: String,
    val aliases: List<String> = listOf(),
    val ipv4: String? = null,
    val ipv6: String? = null,
) {
    fun displayAlias(): String? =
        when {
            aliases.isNotEmpty() -> aliases.joinToString(", ")
            else -> listOfNotNull(ipv4, ipv6).joinToString(", ").ifBlank { null }
        }

    companion object {
        fun parse(
            service: String,
            declaration: ListOrMap?,
        ): List<NetworkBinding> =
            when (declaration) {
                is OrList -> parseList(service, declaration.list)
                is OrMap -> parseYaml(service, declaration.map)
                else -> listOf()
            }

        private fun parseList(
            service: String,
            declaration: List<String>,
        ): List<NetworkBinding> = declaration.map { NetworkBinding(it, service) }

        private fun parseYaml(
            service: String,
            declaration: YAML,
        ): List<NetworkBinding> =
            declaration.mapValues { it.value?.asYaml() ?: emptyMap() }.map { (name, props) ->
                when {
                    props.containsKey("aliases") ->
                        NetworkBinding(name, service, aliases = props.getListByPath("aliases", listOf()))

                    else ->
                        NetworkBinding(name, service, ipv4 = props["ipv4_address"]?.toString(), ipv6 = props["ipv6_address"]?.toString())
                }
            }
    }
}
