package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class NamedLimitTest {

    final static String NAME = "rate-limiter-name";

    @Path("/named-resource-limiter-test")
    @Rate(id = NAME)
    public static class Resource{
        @Path("/home")
        public void home() {}
    }

    WebRateLimiterRegistry registries;

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
        WebRateLimiterContext config =
                WebRateLimiterContextJavaee.builder()
                .properties(props)
                .build();
        registries = WebRateLimiterRegistryJavaee.of(config);
        registries.createRateLimiterFactory();
    }

    @Test
    public void shouldHaveAMatcherRegisteredForCustomName() {
        assertTrue(registries.hasMatcher(NAME));
    }

    @Test
    public void shouldBeRateLimited() {
        assertTrue(registries.isRegistered(NAME));
    }
}
