package ch.derlin.dcvizmermaid.data

import ch.derlin.dcvizmermaid.data.VolumeBinding.VolumeType


fun link(to: String, alias: String? = null) = Service.Link("service", to, alias)
fun parseLink(s: String) = Service.Link.parse("service", s)

fun port(internal: Int, external: Int? = null) = PortBinding("service", internal, external ?: internal)
fun parsePort(p: Any) = PortBinding.parse("service", p)

fun volumeBinding(source: String?, target: String, ro: Boolean = false, type: VolumeType = VolumeType.BIND) =
    VolumeBinding("service", source, target, ro = ro, type = type)

fun parseVolumeBinding(v: Any) = VolumeBinding.parse("service", v)
