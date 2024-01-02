package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

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
        };
        ResourceLimiterConfig config =
                ResourceLimiterConfigJavaee.builder()
                .properties(props)
                .build();
        registries = ResourceLimiterRegistryJavaee.of(config);
        registries.createResourceLimiter();
    }

    @Test
    public void shouldHaveAMatcherRegisteredForCustomName() {
        assertTrue(registries.hasMatching(NAME));
    }

    @Test
    public void shouldBeRateLimited() {
        assertTrue(registries.isRateLimited(NAME));
    }
}
