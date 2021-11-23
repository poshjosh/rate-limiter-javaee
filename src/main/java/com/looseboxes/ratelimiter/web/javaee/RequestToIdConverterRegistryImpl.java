package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverterRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RequestToIdConverterRegistryImpl extends RequestToIdConverterRegistry<ContainerRequestContext> {

    @Inject
    public RequestToIdConverterRegistryImpl(
            RequestToIdConverter<ContainerRequestContext> defaultRequestToIdConverter,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(defaultRequestToIdConverter, rateLimiterConfigurer);
    }
}
