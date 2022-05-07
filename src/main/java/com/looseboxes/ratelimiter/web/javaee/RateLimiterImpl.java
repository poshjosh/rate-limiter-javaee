package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.DefaultRateLimiterConfig;
import com.looseboxes.ratelimiter.DefaultRateLimiterFactory;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.util.Nullable;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterImpl extends WebRequestRateLimiter<ContainerRequestContext> {

    public RateLimiterImpl(RateLimitProperties properties,
                           @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        this(
                properties,
                new RateLimiterConfigurationSource<>(
                        new DefaultMatcherRegistry<>(
                                new RequestToUriConverter(), new ClassPathPatternsProvider(), new MethodPathPatternsProvider()
                        ),
                        new DefaultRateLimiterConfig<>(),
                        new DefaultRateLimiterFactory<>(),
                        rateLimiterConfigurer
                )
        );
    }

    public RateLimiterImpl(RateLimitProperties properties,
                           RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource) {
        super(
                properties,
                rateLimiterConfigurationSource,
                new RateLimiterNodeContext<>(
                        properties,
                        rateLimiterConfigurationSource,
                        new ResourceClassesSupplierImpl(properties).get(),
                        new ClassAnnotationProcessor()
                )
        );
    }
}
