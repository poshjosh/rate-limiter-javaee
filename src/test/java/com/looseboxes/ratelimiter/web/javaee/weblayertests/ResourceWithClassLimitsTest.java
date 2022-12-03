package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

public class ResourceWithClassLimitsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithClassLimits.class);
    }

    @Test
    public void shouldFailWhenClassLimitIsExceeded() {

        final String endpoint = ApiEndpoints.CLASS_LIMITS_HOME;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}