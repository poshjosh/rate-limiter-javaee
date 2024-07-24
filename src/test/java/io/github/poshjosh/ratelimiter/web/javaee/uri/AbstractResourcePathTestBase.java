package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertEqual;

abstract class AbstractResourcePathTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourcePathTestBase.class);

    abstract ResourcePath pathPatterns(String... uris);

    @Test
    public void shouldCombine() {
        LOG.debug("shouldCombine()");
        ResourcePath resourcePath = pathPatterns("/numbers");
        ResourcePath result =  resourcePath.combine(new MethodLevelResourcePath("/1/**", "/2/*"));
        ResourcePath expected = new MethodLevelResourcePath("/numbers/1/**", "/numbers/2/*");
        assertEqual(result.getPatterns(), expected.getPatterns());
    }
}