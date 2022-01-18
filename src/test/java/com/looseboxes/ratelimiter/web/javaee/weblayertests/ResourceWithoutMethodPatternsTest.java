package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertThrows;

public class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithoutMethodPatterns.class);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }
}
