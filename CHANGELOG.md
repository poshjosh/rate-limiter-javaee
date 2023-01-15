# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ Unreleased ]

-

## [ [0.2.0](https://github.com/poshjosh/rate-limiter-javaee/tree/v0.2.0) ] - 2023-01-08

### Added

- Implement matching of requests via annotation `@RateRequestIf`
- Implement and use `UnmodifiableRegistry`
- Add alias `value` as alias for `permits` property in `@Rate`

### Changed

- Rename package `com.looseboxes` to `io.github.poshjosh`
- Update README
- Move `@Rate` and `@RateGroup` from package `ratelimiter.annotations` to `ratelimiter.annotation`

### Removed

- Remove methods `RateCache.cache` and `RateCache.listener`

## [ [0.1.0](https://github.com/poshjosh/rate-limiter-javaee/tree/v0.1.0) ] - 2023-01-07

### Added

- Add `name` field to `@RateLimit` annotation
- Add builder like methods to `ResourceLimiter` for setting `RateCache` and `UsageListener`
- Support single `Rate` per config in properties. Was previously a list of `Rate`s

### Changed

- Update README.md
- Rename all `of()` to `ofDefaults()`
- Change `@RateLimit.limit` to `@RateLimit.permits`
- Change default `timeUnit` in `@RateLimit` to `TimeUnit.SECONDS`
- Rename `WebResourceLimiterConfig` to `ResourceLimiterConfig`
- Make `RateLimiterRegistry` implement Registries
- Rename method `AnnotationProcessor.ofRates` to `AnnotationProcessor.ofDefaults`
- Rename `PatternMatchingResourceLimiter` to `MatchedResourceLimiter`
- Rename `RateLimit` annotation to `Rate`
- Rename `RateLimitGroup` to `RateGroup`
- Change default `@Rate.duration` to 1

### Removed

- Remove annotation `@Nullable`
- Remove `Registries.factories()`
- Remove the value-related generic from `RateCache`. The value now has a fixed type of `Bandwidths`

## [ [0.0.9](https://github.com/poshjosh/rate-limiter-javaee/tree/v0.0.9) ] - 2022-12-30

### Added

- Add more Bandwidth tests

## [ [0.0.8](https://github.com/poshjosh/rate-limiter-javaee/tree/v0.0.8) ] - 2022-12-29

### Added

- Separate repository for annotations: [rate-limiter-annotation](https://github.com/poshjosh/rate-limiter-annotation)
- Change log