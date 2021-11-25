package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateRecordedListener;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterConfigurationSourceImpl extends
        RateLimiterConfigurationSource<ContainerRequestContext> {

    @Inject
    public RateLimiterConfigurationSourceImpl(
            RequestToIdConverter<ContainerRequestContext> defaultRequestToIdConverter,
            RateCache<Object> rateCache,
            RateSupplier rateSupplier,
            RateRecordedListener rateRecordedListener,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(defaultRequestToIdConverter, rateCache, rateSupplier, rateRecordedListener, rateLimiterConfigurer);
    }
}
