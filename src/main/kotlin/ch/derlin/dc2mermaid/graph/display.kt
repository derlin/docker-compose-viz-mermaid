package ch.derlin.dc2mermaid.graph

enum class GraphOrientation {
    LR, RL, TB, BT
}

enum class Shape {
    NONE, RECT_ROUNDED, CIRCLE, HEXAGON, PARALLELOGRAM, CYLINDER, STADIUM;

    fun format(id: Any, name: Any) = when (this) {
        NONE -> "$name"
        RECT_ROUNDED -> "$id($name)"
        CIRCLE -> "$id(($name))"
        HEXAGON -> "$id{{$name}}"
        PARALLELOGRAM -> "$id{{$name}}"
        CYLINDER -> "$id[($name)]"
        STADIUM -> "$id([$name])"
    }
}

enum class CONNECTOR {
    ARROW, DOT_ARROW,
    X, DOT_X, DOT_DBL_X,
    DBL_ARROW, DOT_DBL_ARROW;

    fun format(text: Any?) = when (this) {
        ARROW -> text?.let { "-- $it -->" } ?: "-->"
        DOT_ARROW -> text?.let { "-. $it .->" } ?: "-.->"

        DBL_ARROW -> text?.let { "<-- $it -->" } ?: "<-->"
        DOT_DBL_ARROW -> text?.let { "<-. $it .->" } ?: "<-.->"

        X -> text?.let { "-- $it --x" } ?: "--x"
        DOT_X -> text?.let { "-. $it .-x" } ?: "-.-x"
        DOT_DBL_X -> text?.let { "x-. $it .-x" } ?: "x-.-x"
    }
}