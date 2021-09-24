package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.data.VolumeBinding.VolumeType

abstract class AbstractTestBase {

    protected fun link(to: String, alias: String? = null) = Service.Link("service", to, alias)
    protected fun parseLink(s: String) = Service.Link.parse("service", s)

    protected fun port(internal: Int, external: Int? = null) = PortBinding("service", internal, external ?: internal)
    protected fun parsePort(p: Any) = PortBinding.parse("service", p)

    protected fun volumeBinding(source: String?, target: String, ro: Boolean = false, type: VolumeType = VolumeType.BIND) =
        VolumeBinding("service", source, target, ro = ro, type = type)

    protected fun parseVolumeBinding(v: Any) = VolumeBinding.parse("service", v)
}