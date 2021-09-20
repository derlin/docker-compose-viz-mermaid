package ch.derlin.dc2mermaid.graph

enum class Shape {
    NONE, ROUND, HEXAGON, CYNLINDRIC;

    fun format(id: Any, name: Any) = when (this) {
        NONE -> "$name"
        ROUND -> "$id(($name))"
        HEXAGON -> "$id{{$name}}"
        CYNLINDRIC -> "$id[($name)]"
    }
}

enum class CONNECTOR {
    ARROW, DOT_ARROW,
    X, DOT_X;

    fun format(text: Any?) = when (this) {
        ARROW -> text?.let { "-- $it -->" } ?: "-->"
        DOT_ARROW -> text?.let { "-. $it .->" } ?: "-.->"
        X -> text?.let { "-- $it --x" } ?: "--x"
        DOT_X -> text?.let { "-. $it .-x" } ?: "-.-x"
    }
}