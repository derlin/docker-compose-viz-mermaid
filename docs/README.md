# Docs

## Requirements

- make
- docker

## Generate assets

Everything under `assets/generated` and `_includes/generated` is generated automatically from tests.
Those tests are however disabled by default. To generate them, use:
```bash
# at the root of the project
./gradlew test -Pgenerate
```

To copy them after re-generation, use the `Makefile` in this folder:
```bash
make update_generated_assets
```

## Run this site locally

Launch Jekyll in a docker container using:
```bash
make serve
```

The local site will be available at http://localhost:4000/docker-compose-viz-mermaid/.
