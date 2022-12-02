package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateRecordedListener;
import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.web.core.RateLimiterRegistry;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.RateLimiterDynamicFeature;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.beans.TestRateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.util.Collection;

@javax.ws.rs.ext.Provider
public class TestRateLimiterDynamicFeature extends RateLimiterDynamicFeature {

    private static final class TestRateLimiterConfigurer implements RateLimiterConfigurer<ContainerRequestContext>{

        private final Logger log = LoggerFactory.getLogger(TestRateLimiterConfigurer.class);

        @Override
        public void configure(RateLimiterRegistry<ContainerRequestContext> registry) {

            registry.registerRateRecordedListener(new RateRecordedListener() {
                @Override
                public void onRateExceeded(Object context, Object resourceId, int recordedHits, Collection<Rate> exceededLimits) {

                    log.warn("Too many requests for: {}, limits: {}", resourceId, exceededLimits);

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
