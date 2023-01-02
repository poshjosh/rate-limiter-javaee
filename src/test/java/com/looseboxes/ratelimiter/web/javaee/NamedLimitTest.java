package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.ElementId;
import com.looseboxes.ratelimiter.annotations.RateLimit;
import com.looseboxes.ratelimiter.util.Rates;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Path("/named-resource-limiter-test")
    @RateLimit(name = NAME)
    public static class Resource{
        @Path("/home")
        public void home() {}
    }

    Registries<ContainerRequestContext> registries;

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
        WebResourceLimiterConfig<ContainerRequestContext> config =
                WebResourceLimiterConfigJaveee.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistry.of(config).init();
    }

    @Test
    public void shouldHaveAResourceLimiterRegisteredForCustomName() {
        assertNotNull(registries.limiters().getOrDefault(NAME, null));
    }

    @Test
    public void shouldNotHaveAResourceLimiterRegisteredForDefaultName() {
        String defaultName = ElementId.of(Resource.class);
        assertNull(registries.limiters().getOrDefault(defaultName, null));
    }
}
