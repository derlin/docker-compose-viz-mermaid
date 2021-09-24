---
title: Home
nav_order: 1
description: "Vizualize docker-compose files with Mermaid."
permalink: /
---

# {{ site.title }}
{: .no_toc }

Inspired from, [docker-compose-viz](https://github.com/pmsipilot/docker-compose-viz),
{{ site.title }} is a tool to transform docker-compose files into mermaid flowcharts.
{: .fs-6 .fw-300 }

{% include collapsible_toc.md %}


## Installation

This is a simple jar, that you can download from:
* [nightly](https://github.com/derlin/docker-compose-viz-mermaid/releases/tag/nightly),
* [releases](https://github.com/derlin/docker-compose-viz-mermaid/releases).

Given a JRE is installed on your machine, run:
```bash
java -jar docker-compose-viz-mermaid-*.jar
```

## Usage

<span class="label label-red">Important</span> Images generation require a network connection.


The best way to understand how it works is to run the tool with the `-h` option:
```text
{% include generated/help.md %}
```

## How to read the flowchart

The generated graph/image should be rather straight-forward (see [examples]({{ site.baseurl }}{% link pages/examples.md %})).

### Links and dependencies

Both links (from `services.<service>.links`) and dependencies (from `services.<service>.depends_on`) are displayed using plain arrows.
In case a link defines an alias (using `container:alias`), it is shown as text on the arrow connector.

{% include graph.md image='links-default.svg' %}

Here, both apis need *db*, and *web* needs both apis. *web* will connect to the search api using the alias *search-api-alias*.

#### Implicit links

If implicit link is enabled, {{ site.title }} will try to deduce implicit dependencies from environment variables, which will be shown
exactly as the links and dependencies above. For a link to be discovered, the environment value must:
1. refer to another service, either through its internal `host:port` or through the exposed host port (using `localhost`, `127.0.01` or
   `${DOCKER_HOST_IP}` as the host part),
2. have a value matching either `host:port`, or `<scheme>:[<driver>:]<host>:<port>[/.*]`.

Here are typical examples:
```yaml
bar:
  ports:
    - 8080:1234
foo:
  environment:
    # links using internal host:port
    LINK_INTERNAL_1: bar:1234
    LINK_INTERNAL_2: https://bar:1234/auth
    LINK_INTERNAL_3: jdbc:postgresql:bar:1234/db
    # links using external port
    LINK_EXTERNAL_1: https://${DOCKER_HOST_IP}:8080/auth
```

### Ports

Ports (from `services.<service>.ports`) are displayed as circle and are linked to containers using plain arrows.
The port shown in the circle is the port exposed on the host, while the internal port is shown as text on the arrow connector (`80:8080`).
If both ports are the same (e.g. `443:443`), or only one port is defined (e.g. `443`), only the port in the circle is shown.

{% include graph.md image='ports-default.svg' %}

If classes are enabled, the color of the port will be gray.

### Volumes

Volume shapes and text depend on the type of volume:
* *host binds* --> hexagonal shape, text matching the path on the host;
* *named volumes* --> rounded rectangle, text matching the name of the volume;
* *tmpfs mount* --> diamond with no text;
* *named pipe* --> banner, text matching the path on the host.

Volumes are pointing to the container using dotted connectors ending with `x`. *read-only* connectors miss the `x` on the volume side.
The text inside the shape is the *source* of the volume (on the host), while the text on the connector is the *target* (inside the
container).

{% include graph.md image='volumes-default.svg' %}

If classes are enabled, the color of the port will be yellowish (light) or purple (dark).

## DBs

The tool tries to automatically detect database services and render them as cylindrical shapes. 
The detection is dumb enough, looking for services named `database`, `db` or well-known database providers (`mysql`, `redis`, etc.).

## About graph rendering

{{ site.title }} outputs the generated graph in mermaid syntax by default. However, it is also able to render the graph directly
in multiple formats (`-f` option). To do so, it takes advantage of online tools, namely:

* [Kroki](https://kroki.io/) for svg rendering (as mermaid live has some issues),
* [Mermaid Live Editor](https://mermaid.live/) for the rest.

If you see any problem in the rendered output, generate the mermaid graph and try using those tools directly.
Don't hesitate to report issues on their own github repositories if required.