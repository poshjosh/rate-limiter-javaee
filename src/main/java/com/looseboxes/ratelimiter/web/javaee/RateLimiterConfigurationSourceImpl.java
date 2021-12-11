package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateExceededListener;
import com.looseboxes.ratelimiter.RateFactory;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RateLimiterProvider;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterConfigurationSourceImpl extends
        RateLimiterConfigurationSource<ContainerRequestContext> {

    @Inject
    public RateLimiterConfigurationSourceImpl(
            RequestToIdConverter<ContainerRequestContext, String> requestToUriConverter,
            RateCache<Object> rateCache,
            RateFactory rateFactory,
            RateExceededListener rateExceededListener,
            RateLimiterProvider rateLimiterProvider,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {

        super(requestToUriConverter, rateCache, rateFactory, rateExceededListener, rateLimiterProvider,
                rateLimiterConfigurer, new ClassIdProvider(), new MethodIdProvider());
    }
}
