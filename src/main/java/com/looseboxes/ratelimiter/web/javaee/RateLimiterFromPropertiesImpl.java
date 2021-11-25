package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationRegistry;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.core.RateLimiterImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterFromPropertiesImpl extends RateLimiterImpl<ContainerRequestContext> {

    @Inject
    public RateLimiterFromPropertiesImpl(
            RateLimitProperties properties,
            RateLimiterConfigurationRegistry<ContainerRequestContext> rateLimiterConfigurationRegistry) {
        super(properties.getRateLimitConfigs(), rateLimiterConfigurationRegistry);
    }
}
