.PHONY: all build test generate-examples lint check format

help:
	@awk 'BEGIN {FS = ": .*##";} /^[$$()% 0-9a-zA-Z_-]+(\\:[$$()% 0-9a-zA-Z_-]+)*:.*?##/ { gsub(/\\:/,":", $$1); printf "  \033[36m%-5s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Build

build-all: ## Build the jars
	./gradlew jar
	./gradlew jar -PnoPlaywright
	./gradlew exec-jar

docker:
	docker build --rm -t derlin/docker-compose-viz-mermaid .

generate-examples: ## Generate docs examples
	./gradlew test -Pgenerate

##@ Development

format: ## Format the codebase
	./gradlew ktlintFormat

##@ Checks

test: ## Run tests
	./gradlew test

lint: ## Lint the codebase
	./gradlew detekt ktlintCheck

check: ## Run tests and lint.
	./gradlew check
