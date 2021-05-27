GIT_TAG := $(shell git describe --tags 2>/dev/null)

.PHONY: build
build:
	./gradlew --console plain -Pversion=${GIT_TAG} clean test assemble

.PHONY: deps
deps:
	./gradlew dependencies
