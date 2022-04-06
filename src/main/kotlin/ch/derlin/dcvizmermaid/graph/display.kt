package ch.derlin.dcvizmermaid.graph

import ch.derlin.dcvizmermaid.data.VolumeBinding.VolumeType

enum class GraphTheme {
    DEFAULT, DARK;

    fun shebang(withBgColor: Boolean = false): String {
        val options = mutableListOf("'theme': '${this.name.lowercase()}'")
        // currently, I couldn't find a way to force the background color using mermaid.ink ...
        if (withBgColor) bgColor().let { options += "'themeVariables': {'clusterBkg': '$it', 'clusterBorder': '$it'}" }
        return "%%{init: {${options.joinToString(", ")}}}%%"
    }

    fun bgColor(): String = when (this) {
        DEFAULT -> "#FFF"
        DARK -> "#444"
    }
}

enum class GraphOrientation {
    LR, RL, TB, BT;

    fun inverse(): GraphOrientation = when (this) {
        LR -> TB
        RL -> BT
        TB -> LR
        BT -> RL
    }
}

interface CssClazz {
    val name: String
    fun styles(theme: GraphTheme): String
}

enum class Shape {
    NONE, RECT_ROUNDED, CIRCLE, HEXAGON, PARALLELOGRAM, CYLINDER, STADIUM, RHOMBUS, ASYM;

    fun format(id: Any, name: Any): String {
        val validName = name.toValidName()
        return when (this) {
            NONE -> if (id == name) validName else "$id[$validName]"
            RECT_ROUNDED -> "$id($validName)"
            CIRCLE -> "$id(($validName))"
            HEXAGON -> "$id{{$validName}}"
            PARALLELOGRAM -> "$id[/$validName/]"
            CYLINDER -> "$id[($validName)]"
            STADIUM -> "$id([$validName])"
            RHOMBUS -> "$id{$validName}"
            ASYM -> "$id>$validName]"
        }
    }
}

enum class CONNECTOR(val labelled: String, val simple: String) {
    LINE("-- %s --", "--"),
    DOT_LINE("-. %s .-", "-.-"),

    ARROW("-- %s -->", "-->"),
    DOT_ARROW("-. %s .->", "-.->"),

    DBL_ARROW("<-- %s -->", "<-->"),
    DOT_DBL_ARROW("<-. %s .->", "<-.->"),

    X("-- %s --x", "--x"),
    DOT_X("-. %s .-x", "-.-x"),
    DOT_DBL_X("x-. %s .-x", "x-.-x"),
    DOT_X_REV("x-. %s .-", "x-.-");

    fun format(text: Any?): String =
        text?.toValidName()?.let { labelled.format(it) } ?: simple
}

fun VolumeType.toShape() = when (this) {
    VolumeType.BIND -> Shape.HEXAGON
    VolumeType.VOLUME -> Shape.STADIUM
    VolumeType.NPIPE -> Shape.ASYM
    VolumeType.TMPFS -> Shape.RHOMBUS
}
