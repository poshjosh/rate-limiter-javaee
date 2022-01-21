package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RateLimiterNodeContext;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

@javax.inject.Singleton
public class RateLimiterImpl extends WebRequestRateLimiter<ContainerRequestContext> {

    @Inject
    public RateLimiterImpl(RateLimitProperties properties,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        this(properties, new RateLimiterConfigurationSourceImpl(rateLimiterConfigurer));
    }

    private RateLimiterImpl(RateLimitProperties properties,
                           RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource) {
        super(
                properties,
                rateLimiterConfigurationSource,
                new RateLimiterNodeContext<>(
                        properties, rateLimiterConfigurationSource,
                        new ResourceClassesSupplierImpl(properties).get(),
                        new ClassAnnotationProcessor()
                )
        );
    }
}
