package ch.derlin.dcvizmermaid.data

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import org.junit.jupiter.api.Test

class PortBindingTest : AbstractTestBase() {

    @Test
    fun `parse short port bindings`() {
        fun port(internal: Int, external: Int? = null) = PortBinding("service", internal, external ?: internal)

        assertAll {
            mapOf(
                3000 to port(3000),
                "3000" to port(3000),
                //"3000-3005",
                "8000:8010" to port(8010, 8000),
                //"9090-9091:8080-8081",
                "49100:22" to port(22, 49100),
                "127.0.0.1:8001:8001" to port(8001),
                //"127.0.0.1:5000-5010:5000-5010",
                "127.0.0.1::5000" to port(5000),
                "6060:6060/udp" to port(6060),
                //"12400-12500:1240" to
            ).forEach { (port, expected) ->
                assertThat(port).transform { parsePort(it) }.isNotNull().isEqualTo(expected)
            }
        }
    }


    @Test
    fun `parse long port bindings`() {
        assertAll {
            mapOf(
                """
                target: 80
                published: 8080
                protocol: tcp
                mode: host
                """.trimIndent() to port(80, 8080),
                """
                target: 80
                protocol: tcp
                mode: host
                """.trimIndent() to null
            ).forEach { (port, expected) ->
                assertThat(port).transform { parsePort(YamlUtils.load(it)) }.isEqualTo(expected)
            }
        }
    }

    @Test
    fun `get internal port if different`() {
        assertAll {
            assertThat(port(3000)).transform { it.internalIfDifferent }.isNull()
            assertThat(port(3000, 1234)).transform { it.internalIfDifferent }.isEqualTo(3000)
        }
    }
}
