package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.web.core.DefaultMatcherRegistry;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;

import javax.ws.rs.container.ContainerRequestContext;

@javax.inject.Singleton
public class RateLimiterConfigurationSourceImpl extends RateLimiterConfigurationSource<ContainerRequestContext> {

    @javax.inject.Inject
    public RateLimiterConfigurationSourceImpl(RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer){
        super(
                new DefaultMatcherRegistry<>(new RequestToUriConverter(), new ClassPathPatternsProvider(), new MethodPathPatternsProvider()),
                new DefaultRateLimiterConfig<>(),
                new DefaultRateLimiterFactory<>(),
                rateLimiterConfigurer);
    }
}
