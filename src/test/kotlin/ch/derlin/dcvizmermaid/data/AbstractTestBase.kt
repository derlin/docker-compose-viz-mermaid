package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.helpers.YamlUtils

abstract class AbstractTestBase {

    protected fun link(to: String, alias: String? = null) = Service.Link("service", to, alias)
    protected fun parseLink(s: String) = Service.Link.parse("service", s)

    protected fun port(internal: Int, external: Int? = null) = PortBinding("service", internal, external ?: internal)
    protected fun parseStringPort(p: String) = PortBinding.parse("service", p)
    protected fun parseYamlPort(p: String) = PortBinding.parse("service", YamlUtils.load(p))

    protected fun volumeBinding(source: String? = null, target: String, ro: Boolean = false) =
        VolumeBinding("service", source, target, ro = ro)

    protected fun parseVolumeBinding(v: Any) = VolumeBinding.parse("service", v)
}