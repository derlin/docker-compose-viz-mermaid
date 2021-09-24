---
title:  Options
nav_order: 2
---

{% include toc.md title=page.title %}

## Output types

The output type is controlled via the `-f` or `--format` options.
The output can be either the mermaid code, or the rendered diagram as image.

It is important to note that except for text/markdown, {{ site.title }} relies on the Mermaid
online tools. This has the advantage of guaranteeing the latest version of mermaid is used,
but means that image generation requires a network connection.

| type | description |
| ---- | ----------- |
| `text` | generates the mermaid diagram, that can be copy-pasted in the mermaid live editor, or added in a readme. |
| `markdown` | same as text, but the diagram is wrapped inside a markdown code block |
| `editor` | generates the link to the mermaid live editor, just click on it to get the live editor populated with the diagram (good if you want a base and then change stuff) |
| `preview` | same as `editor`, but the link points to a preview page |
| `png` | generates the diagram as png, and saves it to a file (`image.png`) by default |
| `svg` | generates the diagram as svg, and saves it to a file (`image.svg`) by default |

<span class="label label-red">!!</span> Some svg can be cropped weirdly (bugs in mermaid).
If you want to have a perfect rendering, use `-f editor` and manually export the svg by clicking on the *download svg* button.

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

Depending on the tool used to render the graph, the background could be set to white or transparent.
This can be problematic depending on how you plan to use your graph (e.g. dark theme on light background or vice-versa).
This tool implements a sort of hack in order to force the background color, which can be used through the option `-b`, `--with-bg`.
The examples on this site using the dark theme use this hack.

For those of you who are curious, the hack is about putting everything into a subgraph, and overriding the subgraph's default background
color (it is unfortunately not possible to force the background color of the graph itself).

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

If you do not want to use this feature, turn of classes generation using `--no-classes`.

## Control what is shown in graphs

There are many options available in order to control what is rendered: ports, volumes, implicit links, etc.
Have a look at the options (`-h`) for more information.