package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverterRegistry;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.core.RateLimiterFromProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterFromPropertiesImpl extends RateLimiterFromProperties<ContainerRequestContext> {

    @Inject
    public RateLimiterFromPropertiesImpl(
            RateLimitProperties properties,
            RequestToIdConverterRegistry<ContainerRequestContext> requestToIdConverterRegistry,
            RateCache<Object> rateCache,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler) {

        super(properties, requestToIdConverterRegistry, rateCache, rateSupplier, rateExceededHandler);
    }
}
