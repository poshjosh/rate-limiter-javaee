package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertEqual;

abstract class AbstractResourcePathTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourcePathTestBase.class);

    abstract ResourcePath<String> pathPatterns(String... uris);

    @Test
    public void shouldCombine() {
        LOG.debug("shouldCombine()");
        ResourcePath<String> resourcePath = pathPatterns("/numbers");
        ResourcePath<String> result =  resourcePath.combine(new MethodLevelResourcePath("/1/**", "/2/*"));
        ResourcePath<String> expected = new MethodLevelResourcePath("/numbers/1/**", "/numbers/2/*");
        assertEqual(result.getPatterns(), expected.getPatterns());
    }
}