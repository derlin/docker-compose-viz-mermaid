#!/usr/bin/env bash

mkdir -p ".bundles_cache"
docker run --rm --name dvcm \
  -v "$PWD:/srv/jekyll" \
  -e BUNDLE_PATH="/srv/jekyll/.bundles_cache" \
  -p 4000:4000 \
  jekyll/builder:3.8 \
  bash -c "gem install bundler && bundle install && bundle exec jekyll serve --host 0.0.0.0 --verbose --config _config.yml,_config_dev.yml"