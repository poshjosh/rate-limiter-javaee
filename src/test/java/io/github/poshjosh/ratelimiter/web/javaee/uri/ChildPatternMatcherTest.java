package io.github.poshjosh.ratelimiter.web.javaee.uri;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.*;

public class ChildPatternMatcherTest {

    private static final Logger LOG = LoggerFactory.getLogger(ChildPatternMatcherTest.class);

    @Test
    public void shouldMatchTrailingQuestionMark() {
        LOG.debug("shouldMatchTrailingQuestionMark");
        PatternMatcher patternMatcher = PatternMatchers.child("/users/?");
        assertTrue(patternMatcher.matches("/users/1"));
        assertFalse(patternMatcher.matches("/users/22"));
        assertFalse(patternMatcher.matches("/fake/users/1"));
    }

    @Test
    public void shouldMatchTrailingAsterix() {
        LOG.debug("shouldMatchTrailingAsterix");
        PatternMatcher patternMatcher = PatternMatchers.child("/users/*");
        assertTrue(patternMatcher.matches("/users/1"));
        assertTrue(patternMatcher.matches("/users/22"));
        assertFalse(patternMatcher.matches("/fake/users/22"));
    }

    @Test
    public void shouldMatchTrailingDoubleAsterix() {
        LOG.debug("shouldMatchTrailingDoubleAsterix");
        final String pattern = "/users/**";
        PatternMatcher patternMatcher = PatternMatchers.child(pattern);
        assertTrue(patternMatcher.matches("/users/1"));
        assertTrue(patternMatcher.matches("/users/1/jane"));
        assertFalse(patternMatcher.matches("/fake/users/22/jane"));
    }


    @Test
    public void shouldMatchOnePathSegment() {
        LOG.debug("shouldMatchOnePathSegment");
        PatternMatcher patternMatcher = PatternMatchers.child("/users");
        assertTrue(patternMatcher.matches("/users"));
        assertFalse(patternMatcher.matches("/users/1"));
        assertFalse(patternMatcher.matches("/before/users"));
    }

    @Test
    public void shouldMatchMultiplePathSegments() {
        LOG.debug("shouldMatchMultiplePathSegments");
        PatternMatcher patternMatcher = PatternMatchers.child("/before/{id}/{name}/after");
        assertTrue(patternMatcher.matches("/before/1/jane/after"));
        assertFalse(patternMatcher.matches("/before/1/jane/afte"));
        assertFalse(patternMatcher.matches("/before/1/jane"));
    }

    @Test
    public void shouldMatchMultiplePathSegmentsAndTrailingDoubleAsterix() {
        LOG.debug("shouldMatchMultiplePathSegmentsAndTrailingDoubleAsterix");
        PatternMatcher patternMatcher = PatternMatchers.child("/before/{id}/{name}/**");
        assertTrue(patternMatcher.matches("/before/1/jane/after/1"));
        assertTrue(patternMatcher.matches("/before/1/jane/after"));
        assertTrue(patternMatcher.matches("/before/1/jane"));
        assertFalse(patternMatcher.matches("/before/1"));
    }
}
