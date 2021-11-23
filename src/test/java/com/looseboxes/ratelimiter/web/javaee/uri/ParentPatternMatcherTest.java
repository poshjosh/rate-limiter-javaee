package com.looseboxes.ratelimiter.web.javaee.uri;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.*;

public class ParentPatternMatcherTest {

    private static final Logger LOG = LoggerFactory.getLogger(ParentPatternMatcherTest.class);

    @Test
    public void shouldNotAcceptTrailingQuestionMark() {
        LOG.debug("shouldNotAcceptTrailingQuestionMark");
        shouldNotAcceptSuffix("?");
    }

    @Test
    public void shouldNotAcceptTrailingAsterix() {
        LOG.debug("shouldNotAcceptTrailingAsterix");
        shouldNotAcceptSuffix("*");
    }

    @Test
    public void shouldNotAcceptTrailingDoubleAsterix() {
        LOG.debug("shouldNotAcceptTrailingDoubleAsterix");
        shouldNotAcceptSuffix("**");
    }

    @Test
    public void shouldMatchOnePathSegment() {
        LOG.debug("shouldMatchOnePathSegment");
        PatternMatcher patternMatcher = PatternMatchers.parent("/users");
        assertTrue(patternMatcher.matches("/users"));
        assertTrue(patternMatcher.matches("/users/1"));
        assertTrue(patternMatcher.matches("/users/1/after"));
        assertFalse(patternMatcher.matches("/before/users"));
    }

    @Test
    public void shouldMatchMultiplePathSegments() {
        LOG.debug("shouldMatchZeroOrMorePathSegments");
        PatternMatcher patternMatcher = PatternMatchers.parent("/before/{id}/{name}/after");
        assertTrue(patternMatcher.matches("/before/1/jane/after"));
        assertFalse(patternMatcher.matches("/before/1/jane/afte"));
        assertFalse(patternMatcher.matches("/before/1/jane"));
    }

    private void shouldNotAcceptSuffix(String suffix) {
        assertThrows(IllegalArgumentException.class, () -> PatternMatchers.parent(suffix));
    }
}
