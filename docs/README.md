# Docs

## Generate assets

Everything under `assets/generated` and `_includes/generated` is generated automatically from tests.
Those tests are however disabled by default. To generate them, use:
```bash
# at the root of the project
./gradlew test -Pgenerate
```

## Run this site locally

Launch Jekyll in a docker container:
```bash
docker run --rm --volume="$PWD:/srv/jekyll" -p 4000:4000 -it jekyll/builder:3.8 bash
```

Then, inside the docker container, run:
```bash
# update bundler
gem install bundler
# do this once
bundle install
# then run the server
bundle exec jekyll serve --host 0.0.0.0 --verbose --config "_config.yml,_config_dev.yml"
```

The local site will be available at http://localhost:4000.