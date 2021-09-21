package ch.derlin.dc2mermaid.graph

fun idGenerator(idPrefix: String): () -> String {
    var num = 0
    return { "$idPrefix${num++}" }
}

fun <T> Iterable<T>.withGeneratedIds(idPrefix: String, block: (id: String, item: T) -> Unit): List<String> {
    val generator = idGenerator(idPrefix)
    return map { item -> generator().also { block(it, item) } }
}

internal fun StringBuilder.appendIndentedLine(line: String) = appendLine("  $line")