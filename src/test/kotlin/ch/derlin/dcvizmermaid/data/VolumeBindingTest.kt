package ch.derlin.dcvizmermaid.data

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import org.junit.jupiter.api.Test

class VolumeBindingTest : AbstractTestBase() {


    @Test
    fun `parse volumes short syntax`() {
        //  TODO: support long version + support global volumes
        assertAll {
            mapOf(
                "/var/postgres.sock:/var/postgres.sock" to volumeBinding("/var/postgres.sock", "/var/postgres.sock"),
                "dbdata:/var/lib/postgresql/data" to volumeBinding("dbdata", "/var/lib/postgresql/data"),
                "/config" to volumeBinding(null, "/config"),
                "/config:ro" to volumeBinding(null, "/config", ro = true),
                "/config:rw" to volumeBinding(null, "/config", ro = false),
            ).forEach { (volume, expected) ->
                assertThat(volume).transform { parseVolumeBinding(it) }.isEqualTo(expected)
            }
        }
    }

    @Test
    fun `parse volumes long syntax`() {
        //  TODO: support long version + support global volumes
        assertAll {
            mapOf(
                """
                type: volume
                source: mydata
                target: /data
                volume:
                  nocopy: true
                """.trimIndent() to volumeBinding("mydata", "/data"),
                """
                type: bind
                source: ./mydata
                target: /data/dir
                read_only: true
                """.trimIndent() to volumeBinding("./mydata", "/data/dir", ro = true).copy(type = VolumeBinding.VolumeType.BIND),
            ).forEach { (volume, expected) ->
                assertThat(volume).transform { parseVolumeBinding(YamlUtils.load(it)) }.isEqualTo(expected)
            }
        }
    }
}