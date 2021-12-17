---
title:  Options
nav_order: 2
---

{% include toc.md title=page.title %}

## Output types

The output type is controlled via the `-f` or `--format` options.
The output can be either the mermaid code, a link to online mermaid tools, or the rendered graph as image:

| type | description |
| ---- | ----------- |
| `text` | generates the mermaid diagram, that can be copy-pasted in the mermaid live editor, or added in a readme. |
| `markdown` | same as text, but the diagram is wrapped inside a markdown code block |
| `editor` | generates the link to the mermaid live editor, just click on it to get the live editor populated with the diagram (good if you want a base and then change stuff) |
| `preview` | same as `editor`, but the link points to a preview page |
| `png` | generates the diagram as png, and saves it to a file (`image.png` by default) |
| `svg` | generates the diagram as svg, and saves it to a file (`image.svg` by default) |

Both `png` and `svg` use a local Chromium instance by default, while `editor` and `preview` will output links to
online opensource tools. See [environment variables]({{ site.baseurl }}{% link pages/options.md %}#environment-variables) to change
those defaults.

<span class="label label-yellow">note</span> I try to keep the local mermaid version up-to-date.
In case the rendering is not what you expect (bug or old version),
use `-f editor` and manually export the svg by clicking on the *download svg* button.

## Theming


| light | dark |
| ----- | ---- |
| {% include graph.md image='simple-default.svg' %} | {% include graph.md image='simple-dark.svg' %} |

The diagrams can be generated using either `dark` or `default` (e.g. light) theme.
Those themes are controlled by using a *shebang* of the following format at the start of the graph:
```text
%%{init: {'theme': 'dark'}}%%
```

**background color**

<span class="label label-blue">info</span>
*If you generate images (PNG, SVG) from {{ site.title }} using the default renderer and pass the `--with-bg` option,
images will be generated with a proper background color automatically*.

Depending on the tool used to render the graph, the background could be set to white or transparent.
This can be problematic depending on how you plan to use your graph (e.g. dark theme on light background or vice-versa).
This tool implements a sort of hack in order to force the background color, which can be used through the option `-b`, `--with-bg`.

For those of you who are curious, the hack is about putting everything into a subgraph, and overriding the subgraph's default background
color, as it is unfortunately not currently possible to force the background color of the graph itself.
The upside is, with this hack in place, any tool using the latest mermaid version will render the background correctly.

## Graph direction

The graphs are generated top-down by default. You can easily change this using the `--dir` option.
See [flowchart orientation](https://mermaid-js.github.io/mermaid/#/flowchart?id=flowchart-orientation) for more details.

## Shape colors

By default, all shapes in a mermaid graph have the same color. To make it easier to read, this tool adds custom CSS classes to the graph,
so that ports and volumes have a distinct color. Note that the classes definitions are "hard-coded" depending on the theme in use.
All examples on this site use the classes option.

If you want different colors and styles, generate the diagram and open it in the mermaid live editor. The classes have the format:
```text
classDef <classname> <styles, e.g. fill:red>
class <id[,id]> <classname>
```

If you do not want to use this feature, turn off classes generation using `--no-classes`.

## Control what is shown in graphs

There are many options available in order to control what is rendered: ports, volumes, implicit links, etc.
Have a look at the [usage]({{ site.baseurl }}{% link pages/index.md %}#usage) for more information.

## Environment variables

Further configuration is available through environment variables:

| Environment variable      | Used For              | Default value |
| :-------------------------| :-------------------- | :------------ |
| `MERMAID_RENDERER`        | rendering             | empty for local renderer (other supported values are `mermaid.ink`, `kroki.io`)  |
| `MERMAID_LIVE_EDITOR_URL` | preview               | <https://mermaid-js.github.io/mermaid-live-editor> |
| `MERMAID_INK_URL`         | preview and rendering | <https://mermaid.ink> |
| `KROKI_URL`               | preview and rendering | <https://kroki.io> |

By default, the rendering of images (SVG, PNG) is done locally using a headless Chromium.
It is possible to change this behaviour and select one of <https://mermaid.ink> or <https://kroki.io>
(doing so will make rending impossible without a network connection). 

All URLs to third-party tools are configurable, which is useful in case you have e.g.
Mermaid Live Editor // Mermaid Ink running inside your organisation.