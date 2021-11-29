package com.looseboxes.ratelimiter.web.javaee.uri;

@FunctionalInterface
interface PatternMatcher {
    boolean matches(String s);
}
