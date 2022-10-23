package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertFalse;
import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertTrue;

public class DisabledRateLimitingTest extends AbstractResourceTest {

    private Boolean originallyDisabled;

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithClassLimits.class);
    }

    @Override
    void init() {
        super.init();
        originallyDisabled = getDynamicFeature().getProperties().getDisabled();
        assertFalse(originallyDisabled);
        getDynamicFeature().getProperties().setDisabled(Boolean.TRUE);
    }

    @Test
    public void shouldSucceedWhenDisabled() {
        assertFalse(originallyDisabled);
        assertTrue(getDynamicFeature().getProperties().getDisabled());
        try {
            final String endpoint = ApiEndpoints.CLASS_LIMITS_HOME;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is originallyDisabled
        }finally{
            getDynamicFeature().getProperties().setDisabled(originallyDisabled);
        }
    }
}