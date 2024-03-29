package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import org.junit.Test;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertFalse;
import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertTrue;

public class ClassLevelPathPatternsTest extends AbstractPathPatternsTestBase {

    @Test
    public void shouldMatchStartOf() {
        PathPatterns<String> pathPatterns = pathPatterns("/numbers");
        assertTrue( pathPatterns.matches("/numbers"));
        assertTrue( pathPatterns.matches("/numbers/1"));
        assertFalse( pathPatterns.matches("/letters/a"));
    }

    PathPatterns<String> pathPatterns(String... uris) {
        return new ClassLevelPathPatterns(uris);
    }
}
