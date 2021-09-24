package ch.derlin.dcvizmermaid

import ch.derlin.dcvizmermaid.graph.CssClazz
import ch.derlin.dcvizmermaid.graph.GraphTheme


object VolumeClazz : CssClazz() {
    override val name: String = "volumes"
    override fun styles(theme: GraphTheme): String = when (theme) {
        GraphTheme.DARK -> "fill:#0f544e,stroke:#23968b"
        GraphTheme.DEFAULT -> "fill:#fdfae4,stroke:#867a22"
    }
}

object PortsClazz : CssClazz() {
    override val name: String = "ports"
    override fun styles(theme: GraphTheme): String = when (theme) {
        GraphTheme.DEFAULT -> "fill:#f8f8f8,stroke:#ccc"
        GraphTheme.DARK -> "fill:#5a5757,stroke:#b6c2ff"
    }
}

object ScpClazz : CssClazz() {
    override val name: String = "ports"
    override fun styles(theme: GraphTheme): String = when (theme) {
        GraphTheme.DEFAULT -> "fill:#fbfff7,stroke:#8bc34a"
        GraphTheme.DARK -> "fill:#3f51b5"
    }
}