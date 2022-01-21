package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;

public class WithoutDynamicFeatureTest extends AbstractResourceTest {

    @Override
    protected Application configure() {
        return ResourceConfig.forApplicationClass(TestApplication.class, getResourceOrProviderClasses());
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithoutClassPatterns.class);
    }

    @Test
    public void shouldSucceedWhenLimitIsExceeded() {

        final String endpoint = ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        // Should not fail since we did not register the required DynamicFeature
        shouldReturnDefaultResult(endpoint);
    }
}
