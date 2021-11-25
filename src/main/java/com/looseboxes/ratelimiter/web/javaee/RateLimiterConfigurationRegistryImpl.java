package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterConfigurationRegistryImpl extends RateLimiterConfigurationRegistry<ContainerRequestContext> {

    @Inject
    public RateLimiterConfigurationRegistryImpl(
            RequestToIdConverter<ContainerRequestContext> defaultRequestToIdConverter,
            RateCache<Object> rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(defaultRequestToIdConverter, rateCache, rateSupplier, rateExceededHandler, rateLimiterConfigurer);
    }
}
