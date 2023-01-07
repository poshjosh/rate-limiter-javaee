package io.github.poshjosh.ratelimiter.web.javaee.uri;

import java.util.function.Function;

@FunctionalInterface
interface PathPatternParser extends Function<String, PatternMatcher> {

}
