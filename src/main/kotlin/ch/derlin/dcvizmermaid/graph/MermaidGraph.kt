package ch.derlin.dcvizmermaid.graph

import ch.derlin.dcvizmermaid.graph.CONNECTOR.ARROW
import ch.derlin.dcvizmermaid.graph.Shape.NONE

class MermaidGraph(val order: String = "TB") {

    private val nodes: MutableMap<String, Node> = mutableMapOf()
    private val links: MutableList<Link> = mutableListOf()
    private val classes: MutableList<String> = mutableListOf()

    fun addNode(name: Any, id: String = name.toString(), shape: Shape? = null) {
        if (id !in nodes) {
            require(shape != null || name == id) { "for shape NONE, id and name must be equal, but $id != $name" }
            nodes[id] = Node(id, name.toString(), shape ?: NONE)
        }
    }

    fun addLink(from: Any, to: Any, connector: CONNECTOR? = null, text: Any? = null) {
        links += Link(from.toString(), to.toString(), connector ?: ARROW, text)
    }

    fun addClass(vararg def: String) {
        classes += def
    }

    fun build(): String {
        val builder = StringBuilder()
        builder.appendLine("flowchart $order")

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

    private class Node(val id: String, val name: String = id, val shape: Shape = NONE) {
        var neverReferenced = true
            private set

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