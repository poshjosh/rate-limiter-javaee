package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
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
                return Collections.emptyList();
            }
            @Override public List<Class<?>> getResourceClasses() {
                return Collections.singletonList(NamedLimitTest.Resource.class);
            }
            @Override public Map<String, Rates> getRateLimitConfigs() {
                return Collections.emptyMap();
            }
        };
        ResourceLimiterConfig<ContainerRequestContext> config =
                ResourceLimiterConfigJaveee.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistryJavaee.of(config);
    }

    @Test
    public void shouldHaveAMatcherRegisteredForCustomName() {
        assertNotNull(registries.matchers().getOrDefault(NAME, null));
    }

    @Test
    public void shouldBeRateLimited() {
        assertTrue(registries.isRateLimited(NAME));
    }
}
