---
title: Home
nav_order: 1
description: "Visualize docker-compose files with Mermaid."
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

Note that {{ site.title }} comes in two flavors:

* normal jar (default): quite heavy (86M+), but contains everything to generate PNG/SVG locally, with no internet access.
  This is best for security, and the rendering is of good quality;
* `_no_local` jar: very light, but uses third-party online tools to generate PNG/SVG (<https://mermaid.ink> by default).
  The quality is sometimes off, and depends on the upstream. Not all features are perfectly supported (e.g. no PNG for kroki.io).

Given a JRE (java 8+) is installed on your machine, run:
```bash
java -jar docker-compose-viz-mermaid-*.jar
```

## Usage

The best way to understand how it works is to run the tool with the `-h` option:
```text
{% include generated/help.md %}
```

## How to read the flowchart

The generated graph should be rather straight-forward (see [examples]({{ site.baseurl }}{% link pages/examples.md %})).

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
* *host binds* → hexagonal shape, text matching the path on the host;
* *named volumes* → rounded rectangle, text matching the name of the volume;
  - *anonymous volumes* → text set to `⋅ ∃ ⋅` (logical operator meaning *there exists*);
* *tmpfs mount* → diamond with no text;
* *named pipe* → banner, text matching the path on the host.

Volumes are pointing to the container using dotted connectors ending with `x` on both sides.
*read-only* connectors miss the `x` on the volume side.
The text inside the shape is the *source* of the volume (on the host), while the text on the connector is the *target* (inside the
container).

{% include graph.md image='volumes-default.svg' %}

If classes are enabled, the color of the port will be yellowish (light theme) or purple (dark theme).

### DBs

The tool tries to automatically detect database services and render them as cylindrical shapes. 
The detection is dumb enough, looking for services named `database`, `db` or well-known database providers (`mysql`, `redis`, etc.).

## Generating images

{{ site.title }} outputs the generated graph in mermaid syntax by default, and is its primary purpose.
Once you have a mermaid graph, a lot of tools and options exist to generate pretty much anything (png, svg, pdf, etc.).

For example, the [mermaid-cli](https://github.com/mermaid-js/mermaid-cli) has many options and capabilities.
Another possibility to get images is to copy-paste your graph in the [Mermaid Live Editor](https//mermaid.live)
(or you the `-f editor` option, which will generate the link to the editor with the graph directly).

**Automatic way**

For convenience, {{ site.title }} also gives you the option to generate images directly in multiple formats
(see [output types]({{ site.baseurl }}{% link pages/options.md %}#output-types)).

To do so, it launches and instruments a headless Chromium using <https://playwright.dev> by default.
The latter will download Chromium the first time it is launched, so be patient and don't worry:
the next execution will be dazzlingly fast.

There is also an option to use well-known online tools to render images, namely <https://mermaid.ink/> or <https://kroki.io>.
Have a look at [environment variables]({{ site.baseurl }}{% link pages/options.md %}#environment-variables) for details.

## Data privacy

This tool processes docker-compose, which can hold sensitive information.
Thus, it is important to state that docker-compose-viz-mermaid only reads the information needed to show the graph and
**doesn't store or spy on anything**.
As long as you only ask for mermaid graphs as text (default output), everything stays on your machine,
and you can always check the output to see if anything sensitive is exposed.

When generating links to the mermaid editor or the live preview
(see [output types]({{ site.baseurl }}{% link pages/options.md %}#output-types)),
the graph is encoded in base64 to generate the link. It is up to you to decide to open the link, and hence "*share*" your graph with
the mermaid online tools (which to my knowledge use client-side JS for rendering, thus is safe from a data privacy standpoint).

Image generation uses a headless Chromium running locally by default. It is thus completely safe.
However, other renderers are available, that can use third-party services
(see [generating images]({{ site.baseurl }}{% link pages/index.md %}#generating-images) and
see [environment variables]({{ site.baseurl }}{% link pages/options.md %}#environment-variables)).
For complete privacy, always use the default renderer.