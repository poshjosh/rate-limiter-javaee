package io.github.poshjosh.ratelimiter.web.javaee.uri;

@FunctionalInterface
interface PatternMatcher {
    boolean matches(String s);
}
