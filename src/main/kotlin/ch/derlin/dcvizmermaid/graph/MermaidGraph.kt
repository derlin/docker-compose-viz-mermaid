package ch.derlin.dcvizmermaid.graph

import ch.derlin.dcvizmermaid.graph.CONNECTOR.ARROW
import ch.derlin.dcvizmermaid.graph.Shape.NONE

class MermaidGraph(val direction: GraphOrientation = GraphOrientation.TB, val theme: GraphTheme = GraphTheme.DEFAULT) {

    private val nodes: MutableMap<String, Node> = mutableMapOf()
    private val links: MutableList<Link> = mutableListOf()
    private val classes: MutableList<String> = mutableListOf()

    fun addNode(name: Any, id: String = name.toString(), shape: Shape? = null) {
        with(id.toValidId()) {
            if (this !in nodes) {
                nodes[this] = Node(this, name.toString(), shape ?: NONE)
            }
        }
    }

    fun addLink(from: Any, to: Any, connector: CONNECTOR? = null, text: Any? = null) {
        links += Link(from.toValidId(), to.toValidId(), connector ?: ARROW, text)
    }

    fun addClass(clazz: CssClazz, ids: Iterable<String>) {
        classes += "classDef ${clazz.name} ${clazz.styles(theme)}"
        classes += "class ${ids.joinToString(",")} ${clazz.name}"
    }

    fun addClass(vararg def: String) {
        classes += def
    }

    fun build(withBackground: Boolean = false): String {
        reset()
        val builder = StringBuilder()
        // adding a subgraph is the only way I found to control the background color...
        // can then be styled using clusterBkg (see GraphTheme)
        // the direction will be inversed though when using a subgraph !
        builder.appendLine(theme.shebang(withBackground))
        builder.appendLine("flowchart " + if (withBackground) direction.inverse() else direction)
        if (withBackground) builder.appendLine("subgraph \" \"")

        links
            .map { formatLink(it) }
            .map { builder.appendIndentedLine(it) }
            .also { if (it.isNotEmpty()) builder.appendLine() }

        nodes.values
            .filter { it.neverReferenced }
            .map { it.format() }
            .map { builder.appendIndentedLine(it) }
            .also { if (it.isNotEmpty()) builder.appendLine() }

        classes.forEach { builder.appendIndentedLine(it) }

        if (withBackground) builder.appendLine("end")
        return builder.toString()
    }

    private fun formatLink(link: Link): String {
        val from = nodes[link.from]
        val to = nodes[link.to]

        require(from != null && to != null) {
            "Got a link to unknown node(s): $from, $to"
        }
        return "${from.format()} ${link.connector.format(link.text)} ${to.format()}"
    }

    private fun reset() {
        nodes.values.forEach { it.neverReferenced = true }
    }

    private class Node(val id: String, val name: String = id, val shape: Shape = NONE) {
        var neverReferenced = true
            internal set

        override fun equals(other: Any?): Boolean = this === other && javaClass == other.javaClass && id == other.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String = "Node(id=$id, name=$name, shape=$shape)"

        fun format(): String {
            val ret = if (neverReferenced) shape.format(id, name) else id
            neverReferenced = false
            return ret
        }
    }

    private class Link(val from: String, val to: String, val connector: CONNECTOR, val text: Any? = null) {
        override fun equals(other: Any?): Boolean = this === other && javaClass == other.javaClass && from == other.from && to == other.to
        override fun hashCode(): Int = 31 * from.hashCode() + to.hashCode()
        override fun toString(): String = "Link(from=$from, to=$to, connector=$connector, text=$text)"
    }
}