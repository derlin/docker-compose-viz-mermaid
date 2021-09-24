package ch.derlin.dcvizmermaid.data

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import org.junit.jupiter.api.Test

class DockerComposeTest : AbstractTestBase() {

    @Test
    fun `parse services`() {
        assertAll {
            listOf(
                """
                web:
                  image: web
                service:
                  image: service
            """.trimIndent(),
                """
                version: 1
                web:
                  image: web
                service:
                  image: service
            """.trimIndent(),
                """
                version: "2.4"
                services:
                    web:
                      image: web
                    service:
                      image: service
            """.trimIndent(),
            ).forEach { content ->
                assertThat(content)
                    .transform { DockerCompose(YamlUtils.load(content)).services }
                    .transform { it.map(Service::name) }
                    .isEqualTo(listOf("web", "service"))
            }
        }
    }

    @Test
    fun `implicit links from refs`() {
        val portBindings = listOf(
            PortBinding("kafka", 9092, 9092),
            PortBinding("web", 8080, 6000),
            PortBinding("web2", 8080, 8080)
        )

        assertAll {
            // positive
            mapOf(
                Service.MaybeReference(true, 9092, "kafka") to link("kafka"),
                Service.MaybeReference(false, 9092, "127.0.0.1") to link("kafka"),
                Service.MaybeReference(true, 8080, "web") to link("web"),
                Service.MaybeReference(false, 8080, "localhost") to link("web2"),
            ).forEach { (ref, expected) ->
                assertThat(ref)
                    .transform { DockerCompose.linksFromMaybeRefs("service", portBindings, listOf(ref)) }
                    .isEqualTo(listOf(expected))
            }

            // negative
            listOf(
                Service.MaybeReference(true, 9092, "web"),
                Service.MaybeReference(true, 6000, "web"),
                Service.MaybeReference(false, 80, "localhost"),
            ).forEach { ref ->
                assertThat(ref)
                    .transform { DockerCompose.linksFromMaybeRefs("service", portBindings, listOf(ref)) }
                    .isEmpty()
            }
        }
    }

    @Test
    fun `process volumes`() {
        val namedVolumes = setOf("app-settings", "config")

        assertAll {
            mapOf(
                // untouched
                volumeBinding("./source", "/config", ro = true) to false,
                volumeBinding(null, "/some/path/xx.sock") to false,

                // transformed
                volumeBinding("app-settings", "/something") to true,
                volumeBinding("config", "/target/path") to true,

                ).forEach { (vb, modified) ->
                assertThat(vb)
                    .transform { DockerCompose.processVolumes(namedVolumes, listOf(vb)) }
                    .isEqualTo(listOf(if (modified) vb.copy(type = VolumeBinding.VolumeType.VOLUME) else vb))
            }
        }
    }
}