package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverterRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RequestToIdConverterRegistryImpl extends
        RequestToIdConverterRegistry<ContainerRequestContext> {

    @Inject
    public RequestToIdConverterRegistryImpl(
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(rateLimiterConfigurer);
    }
}
