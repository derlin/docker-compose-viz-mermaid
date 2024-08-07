package ch.derlin.dcvizmermaid.graph

interface CssClazz {
    val name: String

    fun styles(theme: GraphTheme): String
}

object VolumeClazz : CssClazz {
    override val name: String = "volumes"

    override fun styles(theme: GraphTheme): String =
        when (theme) {
            GraphTheme.DARK -> "fill:#0f544e,stroke:#23968b"
            GraphTheme.DEFAULT -> "fill:#fdfae4,stroke:#867a22"
        }
}

object PortsClazz : CssClazz {
    override val name: String = "ports"

    override fun styles(theme: GraphTheme): String =
        when (theme) {
            GraphTheme.DEFAULT -> "fill:#f8f8f8,stroke:#ccc"
            GraphTheme.DARK -> "fill:#5a5757,stroke:#b6c2ff"
        }
}

object NetworksClazz : CssClazz {
    override val name: String = "nets"

    override fun styles(theme: GraphTheme): String =
        when (theme) {
            GraphTheme.DEFAULT -> "fill:#fbfff7,stroke:#8bc34a"
            GraphTheme.DARK -> "fill:#3f51b5,stroke:#81B1DB"
        }
}
