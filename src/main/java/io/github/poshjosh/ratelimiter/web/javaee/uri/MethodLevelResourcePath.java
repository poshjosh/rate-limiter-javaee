package io.github.poshjosh.ratelimiter.web.javaee.uri;

public class MethodLevelResourcePath extends ResourcePathImpl {
    public MethodLevelResourcePath(String... patterns) {
        super(PatternMatchers::child, patterns);
    }
}
