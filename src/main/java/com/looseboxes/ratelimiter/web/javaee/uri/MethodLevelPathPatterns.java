package com.looseboxes.ratelimiter.web.javaee.uri;

public class MethodLevelPathPatterns extends PathPatternsImpl{
    public MethodLevelPathPatterns(String... patterns) {
        super(PatternMatchers::child, patterns);
    }
}
