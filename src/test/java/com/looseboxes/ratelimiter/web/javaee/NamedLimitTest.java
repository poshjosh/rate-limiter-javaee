package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotations.Rate;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Path("/named-resource-limiter-test")
    @Rate(name = NAME)
    public static class Resource{
        @Path("/home")
        public void home() {}
    }

    ResourceLimiterRegistry registries;

    @Before
    public void setupRateLimiting() {
        RateLimitProperties props = new RateLimitProperties() {
            @Override public List<String> getResourcePackages() {
                return Collections.singletonList(NamedLimitTest.class.getPackage().getName());
            }
            @Override public Map<String, Rates> getRateLimitConfigs() {
                return Collections.emptyMap();
            }
        };
        ResourceLimiterConfig<ContainerRequestContext> config =
                ResourceLimiterConfigJaveee.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistry.of(config);
    }

    @Test
    public void shouldHaveAResourceLimiterRegisteredForCustomName() {
        assertNotNull(registries.limiters().getOrDefault(NAME, null));
    }

    @Test
    public void shouldBeRateLimited() {
        assertTrue(registries.isRateLimited(NAME));
    }
}
