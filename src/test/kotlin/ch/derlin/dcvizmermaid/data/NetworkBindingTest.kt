package ch.derlin.dcvizmermaid.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import ch.derlin.dcvizmermaid.helpers.YamlUtils.getListOrMapByPath
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NetworkBindingTest {

    companion object {
        const val SERVICE_NAME = "some-service"

        private fun parseNetwork(src: String) =
            NetworkBinding.parse(SERVICE_NAME, YamlUtils.load(src).getListOrMapByPath("networks"))

        private fun assertNetworkBindingsEqual(yaml: String, expected: List<NetworkBinding>) {
            assertThat(yaml).transform { parseNetwork(it) }.isEqualTo(expected)
        }
    }

    @Test
    fun `parse empty network`() {
        assertNetworkBindingsEqual("networks:", listOf())
    }

    @Test
    fun `parse network list`() {
        fun network(name: String) = NetworkBinding(name, service = SERVICE_NAME)

        val yaml = """
            networks:
            - foo
            - bar
        """.trimIndent()

        assertNetworkBindingsEqual(yaml, listOf(network("foo"), network("bar")))
    }

    @Test
    fun `parse networks with aliases`() {
        fun network(name: String, vararg aliases: String) =
            NetworkBinding(name, service = SERVICE_NAME, aliases = aliases.toList())

        val yaml = """
            networks:
              new:
                aliases:
                  - database
                  - db
              legacy:
                aliases:
                  - mysql
              dummy:
        """.trimIndent()

        assertNetworkBindingsEqual(
            yaml,
            listOf(
                network("new", "database", "db"),
                network("legacy", "mysql"),
                network("dummy")
            )
        )
    }

    @Test
    fun `parse networks with ips`() {
        val someIpV4 = (1..4).joinToString(".") { Random.nextInt(255).toString() }
        val someIpV6 = (1..8).joinToString(":") { Integer.toHexString(Random.nextInt(65536)) }

        fun network(name: String, ipv4: Boolean = false, ipv6: Boolean = false) =
            NetworkBinding(name, service = SERVICE_NAME, ipv4 = if (ipv4) someIpV4 else null, ipv6 = if (ipv6) someIpV6 else null)

        val yaml = """
            networks:
              both:
                ipv4_address: $someIpV4
                ipv6_address: $someIpV6
              onlyV4:
                ipv4_address: $someIpV4
              onlyV6:
                ipv6_address: $someIpV6
        """.trimIndent()

        assertNetworkBindingsEqual(
            yaml,
            listOf(
                network("both", ipv4 = true, ipv6 = true),
                network("onlyV4", ipv4 = true),
                network("onlyV6", ipv6 = true)
            )
        )
    }
}
