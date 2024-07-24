package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertFalse;
import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertTrue;

public class MethodLevelResourcePathTest extends AbstractResourcePathTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLevelResourcePathTest.class);

    @Test
    public void shouldMatchSinglePathVariable() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        ResourcePath<String> resourcePath = pathPatterns("/{id}");
        assertTrue( resourcePath.matches("/1"));
        assertFalse( resourcePath.matches("/1/fake"));
    }

    @Test
    public void shouldMatchMultiplePathVariables() {
        LOG.debug("#shouldMatchSinglePathVariable()");
        ResourcePath<String> resourcePath = pathPatterns("/before/{id}/{name}");
        assertTrue( resourcePath.matches("/before/1/jane"));
        assertFalse( resourcePath.matches("/1/jane"));
        assertFalse( resourcePath.matches("/before/1"));
        assertFalse( resourcePath.matches("/before/1/jane/fake"));
    }

    @Test
    public void shouldMatchSingleAsterix() {
        LOG.debug("#shouldMatchSingleAsterix()");
        ResourcePath<String> resourcePath = pathPatterns("/*");
        // TODO Issue #1 - Make this test case pass
        //assertTrue( resourcePath.matches("/"));
        assertTrue( resourcePath.matches("/numbers"));
        // TODO Issue #1 - Make this test case pass
        //assertFalse( resourcePath.matches("/numbers/1"));
    }

    @Test
    public void shouldMatchSingleQuestionMark() {
        LOG.debug("#shouldMatchSingleQuestionMark()");
        ResourcePath<String> resourcePath = pathPatterns("/?");
        assertTrue( resourcePath.matches("/a"));
        assertFalse( resourcePath.matches("/"));
        assertFalse( resourcePath.matches("/numbers"));
    }

    @Test
    public void shouldMatchDoubleAsterix() {
        LOG.debug("shouldMatchDoubleAsterix()");
        ResourcePath<String> resourcePath = pathPatterns("/**");
        assertTrue( resourcePath.matches("/numbers"));
        assertTrue( resourcePath.matches("/numbers/1"));
    }

    ResourcePath<String> pathPatterns(String... uris) {
        return new MethodLevelResourcePath(uris);
    }
}
