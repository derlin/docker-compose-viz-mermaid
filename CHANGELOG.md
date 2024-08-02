# Changelog

## [1.3.0](https://github.com/derlin/docker-compose-viz-mermaid/compare/v1.2.0...v1.3.0) (2024-08-02)


### 🚀 Features

* provide an ARM docker image ([4e76d1a](https://github.com/derlin/docker-compose-viz-mermaid/commit/4e76d1a296a9f43bddce5c28ed9711b5075492ac))


### 🐛 Bug Fixes

* add MariaDB to known databases ([b230942](https://github.com/derlin/docker-compose-viz-mermaid/commit/b230942dcb40a67055cce15745c2ff6f668c3008))
* properly quote labels with dots ([a6a5e35](https://github.com/derlin/docker-compose-viz-mermaid/commit/a6a5e3542a8b180361d16a0920956a05a34dea4b))
* update dependencies ([4c0f84c](https://github.com/derlin/docker-compose-viz-mermaid/commit/4c0f84c7acfeff2bce023f00ee6fa603489522c6))
* update docker image ([eaf82ba](https://github.com/derlin/docker-compose-viz-mermaid/commit/eaf82ba2bb964a4762c7d9773c493f50691ddcd4))


### 💬 Documentation

* update gems and examples, add Makefile ([a4bbae8](https://github.com/derlin/docker-compose-viz-mermaid/commit/a4bbae86ae03f0462d7a4ab6e545d11db777891c))


### ⟲ CI

* run MermaidRenderer tests again ([e2ace90](https://github.com/derlin/docker-compose-viz-mermaid/commit/e2ace90eb5f1b39361142d3a29904ab73437f66c))

## [1.2.0](https://github.com/derlin/docker-compose-viz-mermaid/compare/v1.1.0...v1.2.0) (2023-07-18)


### 🐛 Bug Fixes

* published port may be string ([2a541b4](https://github.com/derlin/docker-compose-viz-mermaid/commit/2a541b40c36c610f9c79fbef93a3cf8163a24a06)), closes [#10](https://github.com/derlin/docker-compose-viz-mermaid/issues/10)
* remove white border in PNG export (dark theme) ([fffbf91](https://github.com/derlin/docker-compose-viz-mermaid/commit/fffbf91258938e3cbd6b34553c8e86fd51597c85))


### 🚀 Features

* add --debug option ([6e42f1b](https://github.com/derlin/docker-compose-viz-mermaid/commit/6e42f1bf4d5948f0b30a3ccf8d3f359b2f9fbf9c))
* add mermaid-cli renderer ([69c516e](https://github.com/derlin/docker-compose-viz-mermaid/commit/69c516e798eb449f336e0c0dd38fc07a2dd4c6ea))
* add png support to KrokiRenderer ([8853bfd](https://github.com/derlin/docker-compose-viz-mermaid/commit/8853bfda40896d83bfe4580248acaa36e2150a11))
* optionally display network bindings ([930b1fd](https://github.com/derlin/docker-compose-viz-mermaid/commit/930b1fd9530b2dd8028bcba3451f8074d7791f36))
* remove hidden scp classes support ([d2f0499](https://github.com/derlin/docker-compose-viz-mermaid/commit/d2f0499be499e538ed515a4287b24e9373a4fa5f))


### 💬 Documentation

* document the mermaid-cli renderer ([7dd2847](https://github.com/derlin/docker-compose-viz-mermaid/commit/7dd28475123565e0c10af09f8c34eaff17d35145))


### ⟲ CI

* build and push docker image ([9779848](https://github.com/derlin/docker-compose-viz-mermaid/commit/9779848febb3e3fc2bf99694fe47b26d733d21a6))
* fix releases missing artifacts ([0c77882](https://github.com/derlin/docker-compose-viz-mermaid/commit/0c77882ea2a598152a0e59d7ec507001789a1ccb))

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
