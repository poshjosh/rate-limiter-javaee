package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class ResourceWithoutClassPatternsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithoutClassPatterns.class);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
