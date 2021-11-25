package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.core.RateLimiterImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RateLimiterFromProperties extends RateLimiterImpl<ContainerRequestContext> {

    @Inject
    public RateLimiterFromProperties(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationRegistry) {
        super(properties.getRateLimitConfigs(), rateLimiterConfigurationRegistry);
    }
}
