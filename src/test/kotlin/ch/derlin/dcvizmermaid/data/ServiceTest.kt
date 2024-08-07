package ch.derlin.dcvizmermaid.data

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import org.junit.jupiter.api.Test

class ServiceTest {
    @Test
    fun `parse link`() {
        assertAll {
            mapOf(
                "db" to link("db"),
                "db:database" to link("db", "database"),
                "redis" to link("redis"),
            ).forEach { (link, expected) ->
                assertThat(link).transform { parseLink(it) }.isEqualTo(expected)
            }
        }
    }

    @Test
    fun `parse depends_on as list or map form`() {
        assertAll {
            listOf(
                """
                depends_on:
                - foo
                - bar
                """,
                """
                depends_on:
                  foo:
                  bar:
                """,
                """
                depends_on:
                  foo:
                    condition: service_healthy
                  bar:
                    condition: service_completed_successfully
                """,
            ).forEach { yaml ->
                assertThat(yaml)
                    .transform { Service("service", YamlUtils.load(yaml)).links().map { it.to } }
                    .isEqualTo(listOf("foo", "bar"))
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
                "http://localhost:3000,http://localhost:4200" to null,
            ).forEach { (env, expected) ->
                assertThat(env).transform { Service.MaybeReference.parse(it) }.isEqualTo(expected)
            }
        }
    }
}
