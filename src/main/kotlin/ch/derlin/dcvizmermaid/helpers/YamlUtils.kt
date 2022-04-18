package ch.derlin.dcvizmermaid.helpers

import org.yaml.snakeyaml.Yaml
import kotlin.reflect.KClass

typealias YAML = Map<String, Any?>

@Suppress("UNCHECKED_CAST")
object YamlUtils {

    val yaml = Yaml()

    fun load(content: String): YAML = yaml.load(content)

    inline fun <reified T> YAML.getListByPath(path: String, default: List<T>): List<T> = getByPath(path)?.let { value ->
        require(value is List<*> && value.all { it is T }) { "Unexpected type for list $value" }
        value as List<T>
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

    // useful to avoid the "uncheck cast" warnings in the calling code
    fun Any.asYaml(): YAML = this as YAML
}
