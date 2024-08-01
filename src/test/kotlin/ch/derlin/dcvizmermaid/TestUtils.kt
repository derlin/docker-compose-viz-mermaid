package ch.derlin.dcvizmermaid

import assertk.assertThat
import assertk.assertions.isEqualTo

internal fun assertIsSuccess(body: () -> Unit) {
    assertThat(body()).isEqualTo(Unit)
}
