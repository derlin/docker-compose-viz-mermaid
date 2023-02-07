# Changelog

## [1.1.0](https://github.com/derlin/docker-compose-viz-mermaid/compare/v1.0.0...v1.1.0) (2023-02-07)


### ⟲ CI

* automate release and adopt conventional commits ([3f8a023](https://github.com/derlin/docker-compose-viz-mermaid/commit/3f8a02366825b595be97851d94e75846dcbf2b13))


### 🚀 Features

* add `--version` parameter ([c16942e](https://github.com/derlin/docker-compose-viz-mermaid/commit/c16942ef38b7a22203916ac2dbeafd87d5715c93))
* improve cli help ([231c970](https://github.com/derlin/docker-compose-viz-mermaid/commit/231c97021cf20aece4437cd74a891cd26a8211c4))


### 💬 Documentation

* mention GitHub support for Mermaid ([3d754f1](https://github.com/derlin/docker-compose-viz-mermaid/commit/3d754f1f464006b39f0f6d5086f6912fc9ab6a32))


### 🐛 Bug Fixes

* depends_on must support conditions ([#5](https://github.com/derlin/docker-compose-viz-mermaid/issues/5)) ([823c565](https://github.com/derlin/docker-compose-viz-mermaid/commit/823c565d22a687761384227bdbac4c2830c6b581)), closes [#4](https://github.com/derlin/docker-compose-viz-mermaid/issues/4)
* missing logger implementation ([b17346d](https://github.com/derlin/docker-compose-viz-mermaid/commit/b17346d5c62c9468d3571774b0d28a61e279486e))

## 1.0.0 (2022-04-05)

### 🚀 Features

* transform docker-compose into beautiful Mermaid-JS visuals
* generation of SVG, PNG or mermaid code
* offline generation of images by default (using an embedded Chromium, nothing leaves your computer !)
* possible to generate images through third-party services (https://kroki.io, https://mermaid.ink)
* support for background colours in images or generated code
