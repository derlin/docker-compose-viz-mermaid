DOCKER_IMAGE := bretfisher/jekyll-serve
SHELL:=/bin/bash

update_generated_assets: ## Copy the assets generated in tests to the docs
	# IMPORTANT: run the ExamplesGenerator in src/tests first!
	cp `find ../build/generated -type f '!' -name '*.png' '!' -name '*.svg'` _includes/generated
	cp `find ../build/generated -type f -name '*.png' -o -name '*.svg'` assets/generated

serve: ## start serving the docs locally
	@echo "Serving docs on http://localhost:4000/docker-compose-viz-mermaid/"
	docker run --rm --name dcvm-docs -v $(PWD):/site -p 4000:4000 $(DOCKER_IMAGE)

update_local_gem: ## remove the local Gemfile.lock
	rm Gemfile.lock



