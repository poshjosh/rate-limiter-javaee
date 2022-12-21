package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateRecordedListener;
import com.looseboxes.ratelimiter.bandwidths.Bandwidths;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.AbstractRateLimiterDynamicFeature;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.beans.TestRateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

@javax.ws.rs.ext.Provider
public class TestRateLimiterDynamicFeature extends AbstractRateLimiterDynamicFeature {

    private static final class TestRateLimiterConfigurer implements RateLimiterConfigurer<ContainerRequestContext>{

        private final Logger log = LoggerFactory.getLogger(TestRateLimiterConfigurer.class);

        @Override
        public void configure(Registries<ContainerRequestContext> registry) {

            registry.listeners().register(new RateRecordedListener() {
                @Override
                public void onRateExceeded(Object context, Object resourceId, int recordedHits, Bandwidths limit) {

                    log.warn("Too many requests for: {}, limits: {}", resourceId, limit);

                    throw new WebApplicationException("Too may requests for: " + resourceId, Response.Status.TOO_MANY_REQUESTS);
                }
            });
        }
    }

    private final RateLimitProperties properties;

    @javax.inject.Inject
    public TestRateLimiterDynamicFeature(RateLimitProperties properties) {
        super(properties, new TestRateLimiterConfigurer());
        this.properties = properties;
    }

    public TestRateLimitProperties getProperties() {
        return (TestRateLimitProperties) properties;
    }
}
