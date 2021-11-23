package com.looseboxes.ratelimiter.web.javaee.weblayer;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertThrows;

public class ResourceWithClassLimitsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithClassLimits.class);
    }

    @Test
    public void shouldFailWhenClassLimitIsExceeded() {

        final String endpoint = ApiEndpoints.CLASS_LIMITS_HOME;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }
}