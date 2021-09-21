package ch.derlin.dc2mermaid.helpers

import org.yaml.snakeyaml.Yaml
import java.io.Writer
import kotlin.reflect.KClass

typealias YAML = Map<String, Any?>

object YamlUtils {

    val yaml = Yaml()

    fun load(content: String): YAML = yaml.load<YAML>(content)

    fun YAML.dumpTo(writer: Writer) {
        writer.write(yaml.dumpAsMap(this))
    }

    fun YAML.deepCopy(): YAML {
        return yaml.load<YAML>(
            yaml.dump(this)
        )
    }


//    fun <T> YAML.getByPath(path: String, type: KClass<T>, default: T): T {
//        var root: YAML = this
//        val segments = path.split(".")
//        segments.dropLast(1).forEach { key ->
//            root = root[key] as? YAML ?: mapOf()
//        }
//        val value = root[segments.last()] ?: default
//        if (value != null) {
//            require(type.isInstance(value)) {
//                "Unexpected class for $value. Expected: ${value.javaClass}, got ${type.java}"
//            }
//        }
//        return value as T
//    }

    inline fun <reified T> YAML.getListByPath(path: String, default: List<T>): List<T> = getByPath(path)?.let { value ->
        require(value is List<*> && value.all { it is T }) { "Unexpected type for list $value" }
        value as List<T>
    } ?: default


    inline fun <reified T> YAML.getByPath(path: String, default: T): T = getByPath(path)?.let {
        require(it is T) { "Wrong type for $it" }
        it
    } ?: default

    fun <T : Any> YAML.getByPath(path: String, type: KClass<T>): T? = getByPath(path)?.let {
        require(type.isInstance(it)) { "Wrong type for $it" }
        it as T
    }

    fun YAML.getByPath(path: String): Any? {
        var root: YAML = this
        val segments = path.split(".")
        segments.dropLast(1).forEach { key ->
            root = root[key] as? YAML ?: mapOf()
        }
        return root[segments.last()]
    }

    fun YAML.mergeWith(overrides: YAML? = null): YAML {
        if (overrides == null || overrides.isEmpty()) return this
        return deepMerge(
            this.deepCopy().toMutableMap(),
            overrides
        ) as YAML
    }

    fun compare(expected: String, actual: String): List<String> {
        return compare(
            yaml.loadAll(expected).toList() as List<YAML>,
            yaml.loadAll(actual).toList() as List<YAML>
        )
    }

    fun compare(expected: List<YAML>, actual: List<YAML>): List<String> {
        if (expected.size != actual.size) {
            return listOf("Wrong number of documents: ${expected.size} != ${actual.size}")
        } else if (expected.size == 1) {
            return compare(expected.first(), actual.first())
        } else {
            val differences = mutableListOf<String>()
            IntRange(0, expected.size - 1).forEach { idx ->
                compare(expected[idx], actual[idx]).forEach { msg -> differences.add("[${idx}]${msg}") }
            }
            return differences
        }
    }

    fun compare(expected: YAML, actual: YAML): List<String> {
        val differences = mutableListOf<String>()
        _compare(expected, actual, "", differences)
        return differences
    }

    private fun _compare(expected: YAML, actual: YAML, root: String = "", vs: MutableList<String>) {
        (expected.keys intersect actual.keys).forEach { key ->
            _compareValues(expected[key], actual[key], "$root[$key]", vs)
        }

        (expected.keys subtract actual.keys).forEach { key ->
            vs.add("$root[$key]: in expected only")
        }

        (actual.keys subtract expected.keys).forEach { key ->
            vs.add("$root[$key]: in actual only")
        }
    }

    private fun _compareLists(expected: List<*>, actual: List<*>, root: String, vs: MutableList<String>) {
        expected.zip(actual).forEachIndexed { index, pair ->
            _compareValues(pair.first, pair.second, "$root[$index]", vs)
        }

        if (expected.size != actual.size) {
            vs.add("$root: expected size is ${expected.size}, but actual is ${actual.size}")
        }
    }

    private fun _compareValues(expected: Any?, actual: Any?, root: String, vs: MutableList<String>) {
        if (_checkNull(expected, actual, root, vs) && _checkType(expected!!, actual!!, root, vs)) {
            if (expected is List<*>) {
                _compareLists(expected, actual as List<*>, root, vs)
            } else if (expected is Map<*, *>) {
                _compare(expected as YAML, actual as YAML, root, vs)
            } else {
                if (!expected.equals(actual)) {
                    vs.add("${root}: values differ, expected=${expected}, actual=${actual}")
                }
            }
        }
    }


    private fun _checkNull(expected: Any?, actual: Any?, yamlPath: String, vs: MutableList<String>): Boolean {
        if (expected == null) {
            if (actual != null) {
                vs.add("${yamlPath}: expected null, was (${actual})")
            }
            return false
        } else if (actual == null) {
            vs.add("${yamlPath}: expected ${expected}, but was null")
            return false
        }
        return true
    }

    private fun _checkType(expected: Any, actual: Any, yamlPath: String, vs: MutableList<String>): Boolean {
        if (expected::class != actual::class) {
            vs.add("${yamlPath}: types differ, ${expected::class}!=${actual::class}")
            return false
        }
        return true
    }

    @SuppressWarnings("unchecked")
    private fun deepMerge(dst: MutableMap<String, Any?>?, src: Map<String, Any?>?): Map<String, Any?>? {
        return when {
            dst == null -> src
            src == null -> dst
            else -> {
                for (key in src.keys) {
                    if (src[key] is Map<*, *> && dst[key] is Map<*, *>) {
                        val originalChild = dst[key] as MutableMap<String, Any?>?
                        val newChild = src[key] as Map<String, Any?>?
                        dst[key] = deepMerge(originalChild, newChild)
                    } else {
                        dst[key] = src[key]
                    }
                }
                dst
            }
        }
    }

}