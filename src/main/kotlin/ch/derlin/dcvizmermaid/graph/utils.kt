package ch.derlin.dcvizmermaid.graph

fun idGenerator(idPrefix: String): () -> String {
    var num = 0
    return { "$idPrefix${num++}" }
}

fun <T> Iterable<T>.withGeneratedIds(idPrefix: String, block: (id: String, item: T) -> Unit): List<String> {
    val generator = idGenerator(idPrefix)
    return map { item -> generator().also { block(it, item) } }
}

internal fun StringBuilder.appendIndentedLine(line: String) = appendLine("  $line")

internal fun Any.toValidId(): String = toString().replace("[^a-zA-Z0-9]".toRegex(), "").ifBlank { "H" + this.hashCode() }

internal fun Any.toValidName() = toString().replace("\"", "'").let {
    if ("[\\w !?_/':,.-]*".toRegex().matchEntire(it) == null) "\"$it\"" else it
}