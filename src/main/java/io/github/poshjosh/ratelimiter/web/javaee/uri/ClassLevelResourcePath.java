package io.github.poshjosh.ratelimiter.web.javaee.uri;

public class ClassLevelResourcePath extends ResourcePathImpl {
    public ClassLevelResourcePath(String... patterns) {
        super(PatternMatchers::parent, patterns);
    }
}
