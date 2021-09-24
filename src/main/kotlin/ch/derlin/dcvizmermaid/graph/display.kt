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

    private fun bgColor(): String = when (this) {
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

abstract class CssClazz {
    abstract val name: String
    abstract fun styles(theme: GraphTheme): String
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

enum class CONNECTOR {
    LINE, DOT_LINE,
    ARROW, DOT_ARROW,
    X, DOT_X, DOT_X_REV, DOT_DBL_X,
    DBL_ARROW, DOT_DBL_ARROW;

    fun format(text: Any?): String {
        val validText = text?.toValidName()
        return when (this) {
            LINE -> validText?.let { "-- $it --" } ?: "--"
            DOT_LINE -> validText?.let { "-. $it .-" } ?: "-.-"

            ARROW -> validText?.let { "-- $it -->" } ?: "-->"
            DOT_ARROW -> validText?.let { "-. $it .->" } ?: "-.->"

            DBL_ARROW -> validText?.let { "<-- $it -->" } ?: "<-->"
            DOT_DBL_ARROW -> validText?.let { "<-. $it .->" } ?: "<-.->"

            X -> validText?.let { "-- $it --x" } ?: "--x"
            DOT_X -> validText?.let { "-. $it .-x" } ?: "-.-x"
            DOT_X_REV -> validText?.let { "x-. $it .-" } ?: "x-.-"
            DOT_DBL_X -> validText?.let { "x-. $it .-x" } ?: "x-.-x"
        }
    }
}

fun VolumeType.toShape() = when (this) {
    VolumeType.BIND -> Shape.HEXAGON
    VolumeType.VOLUME -> Shape.STADIUM
    VolumeType.NPIPE -> Shape.ASYM
    VolumeType.TMPFS -> Shape.RHOMBUS
}