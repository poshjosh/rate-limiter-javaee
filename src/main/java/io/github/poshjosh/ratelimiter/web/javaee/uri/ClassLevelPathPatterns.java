package io.github.poshjosh.ratelimiter.web.javaee.uri;

public class ClassLevelPathPatterns extends PathPatternsImpl {
    public ClassLevelPathPatterns(String... patterns) {
        super(PatternMatchers::parent, patterns);
    }
}
