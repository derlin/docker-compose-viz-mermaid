package ch.derlin.dcvizmermaid.helpers

import org.yaml.snakeyaml.Yaml
import kotlin.reflect.KClass

typealias YAML = Map<String, Any?>

object YamlUtils {

    val yaml = Yaml()

    fun load(content: String): YAML = yaml.load(content)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> YAML.getListByPath(path: String, default: List<T>): List<T> = getByPath(path)?.let { value ->
        require(value is List<*> && value.all { it is T }) { "Unexpected type for list $value" }
        value as List<T>
    } ?: default

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> YAML.getByPath(path: String, type: KClass<T>): T? = getByPath(path)?.let {
        require(type.isInstance(it)) { "Wrong type for $it" }
        it as T
    }

    @Suppress("UNCHECKED_CAST")
    fun YAML.getByPath(path: String): Any? {
        var root: YAML = this
        val segments = path.split(".")
        segments.dropLast(1).forEach { key ->
            root = root[key] as? YAML ?: mapOf()
        }
        return root[segments.last()]
    }
}
