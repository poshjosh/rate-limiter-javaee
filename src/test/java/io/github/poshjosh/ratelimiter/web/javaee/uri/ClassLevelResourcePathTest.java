package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.junit.Test;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertFalse;
import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertTrue;

public class ClassLevelResourcePathTest extends AbstractResourcePathTestBase {

    @Test
    public void shouldMatchStartOf() {
        ResourcePath resourcePath = pathPatterns("/numbers");
        assertTrue( resourcePath.matches("/numbers"));
        assertTrue( resourcePath.matches("/numbers/1"));
        assertFalse( resourcePath.matches("/letters/a"));
    }

    ResourcePath pathPatterns(String... uris) {
        return new ClassLevelResourcePath(uris);
    }
}
