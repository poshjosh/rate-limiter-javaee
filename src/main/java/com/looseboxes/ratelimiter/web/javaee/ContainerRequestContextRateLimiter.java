package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.util.Nullable;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class ContainerRequestContextRateLimiter extends WebRequestRateLimiter<ContainerRequestContext> {

    @Inject
    public ContainerRequestContextRateLimiter(RateLimitProperties properties,
                                              @Nullable Provider<RateLimiterConfigurer> rateLimiterConfigurerProvider) {
        this(properties, new RateLimiterConfiguration(), rateLimiterConfigurerProvider == null ? null : rateLimiterConfigurerProvider.get());
    }

    private ContainerRequestContextRateLimiter(RateLimitProperties properties,
                                              RateLimiterConfiguration rateLimiterConfiguration,
                                              @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(properties, rateLimiterConfiguration.rateLimiterConfigurationSource(rateLimiterConfigurer, null),
                rateLimiterConfiguration.resourceClassesSupplier(properties).get(), rateLimiterConfiguration.annotationProcessor());
    }
}
