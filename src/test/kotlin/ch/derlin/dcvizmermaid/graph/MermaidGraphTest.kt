package ch.derlin.dcvizmermaid.graph


import assertk.assertAll
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

class MermaidGraphTest {

    @Test
    fun `add node ensures valid ids`() {
        val n1 = "some_node-1"
        val n2 = "@@node-#2"

        val graph = MermaidGraph()
        graph.addNode(n1)
        graph.addNode(n2, shape = Shape.HEXAGON)
        graph.addNode("n3")

        graph.addLink(n1, n2)
        graph.addLink(n2, "n3")
        graph.addLink(n1, "n3")

        assertThat(buildAndGetLines(graph)).containsExactlyInAnyOrder(
            "somenode1[$n1] --> node2{{$n2}}",
            "somenode1 --> n3",
            "node2 --> n3"
        )
    }

    @Test
    fun `nodes should be unique`() {
        val nodes = (1..9).map { "S$it" }
        val graph = MermaidGraph()

        (nodes + nodes).shuffled().forEach { graph.addNode(it) }
        assertThat(buildAndGetLines(graph))
            .containsExactlyInAnyOrder(*nodes.toTypedArray())
    }

    @Test
    fun `duplicate nodes, first shape added wins`() {
        val graph = MermaidGraph()
        graph.addNode("node rounded", id = "n", shape = Shape.RECT_ROUNDED)
        graph.addNode("node hexa", id = "n", shape = Shape.HEXAGON)
        graph.addNode("node", id = "n", shape = null)

        assertThat(buildAndGetLines(graph)).containsOnly("n(node rounded)")
    }

    @Test
    fun `only first occurrence has a shape`() {
        val graph = MermaidGraph()
        graph.addNode("r", shape = Shape.RECT_ROUNDED)
        graph.addNode("h", shape = Shape.HEXAGON)
        graph.addNode("c", shape = Shape.CYLINDER)

        graph.addLink("r", "h")
        graph.addLink("r", "c")
        graph.addLink("h", "c")

        assertThat(buildAndGetLines(graph)).containsExactly(
            "r(r) --> h{{h}}",
            "r --> c[(c)]",
            "h --> c"
        )
    }

    @Test
    fun `unreferenced nodes are automatically added`() {
        val graph = MermaidGraph()
        (1..5).forEach { graph.addNode("N$it") }

        graph.addLink("N1", "N3")
        graph.addLink("N1", "N5")

        assertThat(buildAndGetLines(graph)).containsExactly(
            "N1 --> N3",
            "N1 --> N5",
            "N2",
            "N4"
        )
    }

    @Test
    fun `links to unknown nodes make the build fail`() {
        assertAll {
            val graph = MermaidGraph()
            graph.addLink("N1", "N2")
            assertThat { graph.build() }.isFailure()
            graph.addNode("N1")
            assertThat { graph.build() }.isFailure()
            graph.addNode("N2")
            assertThat { graph.build() }.isSuccess()
        }
    }

    @Test
    fun `test full graph`() {
        val graph = MermaidGraph()
        graph.addNode("db", shape = Shape.CIRCLE)
        graph.addNode("web")
        graph.addNode("service")
        graph.addNode("none", "n", shape = Shape.HEXAGON)

        graph.addLink("service", "db", connector = CONNECTOR.DOT_DBL_X)
        graph.addLink("web", "service", connector = CONNECTOR.DOT_ARROW, text = "REST")

        graph.addClass("classDef W", "class web W")

        assertThat(graph.build()).isEqualTo(
            """
        flowchart TB
          service x-.-x db((db))
          web -. REST .-> service
        
          n{{none}}
        
          classDef W
          class web W
        """.trimIndent() + "\n"
        )
    }

    private fun buildAndGetLines(graph: MermaidGraph): List<String> =
        graph.build().lines().drop(1).filter { it.isNotEmpty() }.map { it.trim() }

}