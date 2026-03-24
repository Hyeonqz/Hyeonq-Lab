# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot 4 lab project for experimenting with reactive programming using Spring WebFlux. Uses Java 25 and Gradle.

- **Stack**: Spring Boot 4.0.4, Spring WebFlux, Spring Data JPA, H2 (in-memory), Actuator
- **Java**: 25
- **Base package**: `io.github.springreactivelab`

## Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "io.github.springreactivelab.SomeTest"

# Run a single test method
./gradlew test --tests "io.github.springreactivelab.SomeTest.methodName"

# Clean build
./gradlew clean build
```

## Architecture Notes

This is an early-stage lab. As features are added, the reactive stack imposes the following architectural constraints:

- **WebFlux**: All HTTP handlers must return `Mono<T>` or `Flux<T>` — no blocking calls on the event loop.
- **JPA + WebFlux**: Spring Data JPA is blocking by default; wrap repository calls with `Schedulers.boundedElastic()` via `Mono.fromCallable(...)` or migrate to R2DBC for fully non-blocking I/O.
- **H2 console**: Available at `/h2-console` (via `spring-boot-h2console` dependency).
- **Actuator**: Health and metrics endpoints available under `/actuator`.