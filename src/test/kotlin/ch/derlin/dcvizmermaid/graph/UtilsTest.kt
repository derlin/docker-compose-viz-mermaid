package ch.derlin.dcvizmermaid.graph

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun `to valid id`() {
        assertAll {
            mapOf(
                "node" to "node",
                "some-id" to "someid",
                "??1--2_@3 " to "123"
            ).forEach { (input, expected) ->
                assertThat(input.toValidId()).isEqualTo(expected)
            }
        }
    }

    @Test
    fun `to valid name`() {
        assertAll {
            mapOf(
                // no quotes
                "node" to "node",
                "/path/to/file.txt" to "/path/to/file.txt",
                "some node" to "some node",
                "some-node ?" to "some-node ?",
                // quotes
                "N@1" to "\"N@1\"",
                "${'$'}{PWD}/test" to "\"${'$'}{PWD}/test\"",
                "a \"name\"" to "a 'name'",
                "a {\"name\"}" to "\"a {'name'}\"",
                "[test] ?" to "\"[test] ?\"",
            ).forEach { (input, expected) ->
                assertThat(input.toValidName()).isEqualTo(expected)
            }
        }
    }
}