package ch.derlin.dc2mermaid.graph

fun <T> Iterable<T>.withGeneratedIds(idPrefix: String, block: (id: String, item: T) -> Unit): List<String> {
    var num = 0
    return map {
        val id = "$idPrefix${num++}";
        block(id, it);
        id
    }
}

internal fun StringBuilder.appendIndentedLine(line: String) = appendLine("  $line")