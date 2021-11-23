package com.looseboxes.ratelimiter.web.javaee.uri;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertEqual;

abstract class AbstractPathPatternsTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPathPatternsTestBase.class);

    abstract PathPatterns<String> pathPatterns(String... uris);

    @Test
    public void shouldCombine() {
        LOG.debug("shouldCombine()");
        PathPatterns<String> pathPatterns = pathPatterns("/numbers");
        PathPatterns<String> result =  pathPatterns.combine(new MethodLevelPathPatterns("/1/**", "/2/*"));
        PathPatterns<String> expected = new MethodLevelPathPatterns("/numbers/1/**", "/numbers/2/*");
        assertEqual(result.getPathPatterns(), expected.getPathPatterns());
    }
}