package ch.derlin.dc2mermaid

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.dc2mermaid.data.Service
import org.junit.jupiter.api.Test

class ServiceTest {

    @Test
    fun `parse link`() {
        assertAll {
            mapOf(
                "db" to link("db"),
                "db:database" to link("db", "database"),
                "redis" to link("redis")
            ).forEach { (link, expected) ->
                assertThat(link).transform { parseLink(it) }.isEqualTo(expected)
            }
        }
    }

    @Test
    fun `parse volumes`() {
        //  TODO: support long version + support global volumes
        assertAll {
            mapOf(
                "/var/run/postgres/postgres.sock:/var/run/postgres/postgres.sock" to volume("/var/run/postgres/postgres.sock"),
                "dbdata:/var/lib/postgresql/data" to volume("/var/lib/postgresql/data", "dbdata"),
                "/volume/without/mount" to null
            ).forEach { (volume, expected) ->
                assertThat(volume).transform { parseVolume(it) }.isEqualTo(expected)
            }
        }
    }

    @Test
    fun `parse maybe reference`() {
        val dockerHostIp = "$" + "{" + "DOCKER_HOST_IP" + "}"
        assertAll {
            mapOf(
                "kafka:9092" to Service.MaybeReference(true, 9092, "kafka"),
                "kafka-bootstrap:9092" to Service.MaybeReference(true, 9092, "kafka-bootstrap"),
                "$dockerHostIp:80" to Service.MaybeReference(false, 80, dockerHostIp),
                "https://$dockerHostIp:1234/path?param=value" to Service.MaybeReference(false, 1234, dockerHostIp),
                "http://localhost:1234/path?param=value" to Service.MaybeReference(false, 1234, "localhost"),
                "127.0.0.1:1234" to Service.MaybeReference(false, 1234, "127.0.0.1"),
                "test:?[0-9]+" to null,
                "true" to null
            ).forEach { (env, expected) ->
                assertThat(env).transform { Service.MaybeReference.parse(it) }.isEqualTo(expected)
            }
        }
    }


    private fun link(to: String, alias: String? = null) = Service.Link("service", to, alias)
    private fun parseLink(s: String) = Service.Link.parse("service", s)

    private fun volume(target: String, mounted: String? = null) = Service.Volume("service", target, mounted ?: target)
    private fun parseVolume(v: String) = Service.Volume.parse("service", v)
}