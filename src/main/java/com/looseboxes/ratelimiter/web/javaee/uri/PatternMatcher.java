package com.looseboxes.ratelimiter.web.javaee.uri;

public interface PatternMatcher {
    boolean matches(String s);
    String getPattern();
}
