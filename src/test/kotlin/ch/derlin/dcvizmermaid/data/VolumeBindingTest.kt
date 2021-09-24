package ch.derlin.dcvizmermaid.data

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import ch.derlin.dcvizmermaid.data.VolumeBinding.VolumeType.*
import ch.derlin.dcvizmermaid.helpers.YamlUtils
import org.junit.jupiter.api.Test

class VolumeBindingTest : AbstractTestBase() {


    @Test
    fun `parse volumes short syntax`() {
        //  TODO: support long version + support global volumes
        assertAll {
            mapOf(
                "/var/postgres.sock:/var/postgres.sock" to volumeBinding("/var/postgres.sock", "/var/postgres.sock", type = BIND),
                "dbdata:/var/lib/postgresql/data" to volumeBinding("dbdata", "/var/lib/postgresql/data", type = BIND),
                "/config" to volumeBinding(null, "/config", type = VOLUME),
                "/config:ro" to volumeBinding(null, "/config", type = VOLUME, ro = true),
                "/config:rw" to volumeBinding(null, "/config", type = VOLUME, ro = false),
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
                type: bind
                source: ./mydata
                target: /data/dir
                read_only: true
                """.trimIndent() to volumeBinding("./mydata", "/data/dir", ro = true, type = BIND),
                """
                type: volume
                source: mydata
                target: /data
                volume:
                  nocopy: true
                """.trimIndent() to volumeBinding("mydata", "/data", type = VOLUME),
                """
                type: tmpfs
                target: /data/dir
                tmpfs:
                  size: 1024
                """.trimIndent() to volumeBinding(null, "/data/dir", type = TMPFS),
                """
                type: npipe
                source: /var/run/docker.sock
                target: /var/run/docker.sock
                """.trimIndent() to volumeBinding("/var/run/docker.sock", "/var/run/docker.sock", type = NPIPE),
            ).forEach { (volume, expected) ->
                assertThat(volume).transform { parseVolumeBinding(YamlUtils.load(it)) }.isEqualTo(expected)
            }
        }
    }
}