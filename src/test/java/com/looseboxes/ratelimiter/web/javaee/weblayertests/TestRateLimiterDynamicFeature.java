package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.ResourceUsageListener;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.AbstractRateLimiterDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

@javax.ws.rs.ext.Provider
public class TestRateLimiterDynamicFeature extends AbstractRateLimiterDynamicFeature {

    private static final class TestResourceLimiterConfigurer implements ResourceLimiterConfigurer<ContainerRequestContext> {

        private final Logger log = LoggerFactory.getLogger(TestResourceLimiterConfigurer.class);

        @Override
        public void configure(Registries<ContainerRequestContext> registry) {

            registry.listeners().register(new ResourceUsageListener() {
                @Override
                public void onRejected(Object context, Object resource, int recordedHits, Object limit) {

                    log.warn("Too many requests for: {}, limits: {}", resource, limit);

                    throw new WebApplicationException("Too may requests for: " + resource, Response.Status.TOO_MANY_REQUESTS);
                }
            });
        }
    }

    private final RateLimitProperties properties;

    @javax.inject.Inject
    public TestRateLimiterDynamicFeature(RateLimitProperties properties) {
        super(properties, new TestResourceLimiterConfigurer());
        this.properties = properties;
    }

    public TestRateLimitProperties getProperties() {
        return (TestRateLimitProperties) properties;
    }
}
