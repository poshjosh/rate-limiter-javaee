package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationRegistry;
import com.looseboxes.ratelimiter.web.core.RateLimiterImpl;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;
import com.looseboxes.ratelimiter.web.javaee.ResourceClassesSupplierImpl;

import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterWebFeatureImpl extends RateLimiterWebFeature {

    public RateLimiterWebFeatureImpl() {
        this(
                new RateLimiterConfigurationRegistryImpl(
                        new RequestToIdConverterImpl(), new RateCacheImpl(), new RateSupplierImpl(),
                        new RateExceededExceptionThrower(), new RateLimiterConfigurerImpl()),
                new RateLimitPropertiesImpl());
    }

    public RateLimiterWebFeatureImpl(
            RateLimiterConfigurationRegistry<ContainerRequestContext> rateLimiterConfigurationRegistry,
            RateLimitProperties rateLimitProperties) {
        super(
                new RateLimiterImpl<>(
                        rateLimitProperties.getRateLimitConfigs(),
                        rateLimiterConfigurationRegistry
                ),
                rateLimiterConfigurationRegistry,
                new ResourceClassesSupplierImpl(rateLimitProperties),
                rateLimitProperties
        );
    }
}
