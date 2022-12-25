package com.looseboxes.ratelimiter.web.javaee.uri;

import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertFalse;
import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertTrue;

public class MethodLevelPathPatternsTest extends AbstractPathPatternsTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelPathPatternsTest.class);

    @Test
    public void shouldMatchSinglePathVariable() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        PathPatterns<String> pathPatterns = pathPatterns("/{id}");
        assertTrue( pathPatterns.matches("/1"));
        assertFalse( pathPatterns.matches("/1/fake"));
    }

    @Test
    public void shouldMatchMultiplePathVariables() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        PathPatterns<String> pathPatterns = pathPatterns("/before/{id}/{name}");
        assertTrue( pathPatterns.matches("/before/1/jane"));
        assertFalse( pathPatterns.matches("/1/jane"));
        assertFalse( pathPatterns.matches("/before/1"));
        assertFalse( pathPatterns.matches("/before/1/jane/fake"));
    }

    @Test
    public void shouldMatchSingleAsterix() {
        LOG.debug("#shouldMatchSingleAsterix()");
        PathPatterns<String> pathPatterns = pathPatterns("/*");
        // TODO - Make this test pass
        //assertTrue( pathPatterns.matches("/"));
        assertTrue( pathPatterns.matches("/numbers"));
        // TODO - Make this test pass
        //assertFalse( pathPatterns.matches("/numbers/1"));
    }

    @Test
    public void shouldMatchSingleQuestionMark() {
        LOG.debug("#shouldMatchSingleQuestionMark()");
        PathPatterns<String> pathPatterns = pathPatterns("/?");
        assertTrue( pathPatterns.matches("/a"));
        assertFalse( pathPatterns.matches("/"));
        assertFalse( pathPatterns.matches("/numbers"));
    }

    @Test
    public void shouldMatchDoubleAsterix() {
        LOG.debug("shouldMatchDoubleAsterix()");
        PathPatterns<String> pathPatterns = pathPatterns("/**");
        assertTrue( pathPatterns.matches("/numbers"));
        assertTrue( pathPatterns.matches("/numbers/1"));
    }

    PathPatterns<String> pathPatterns(String... uris) {
        return new MethodLevelPathPatterns(uris);
    }
}
