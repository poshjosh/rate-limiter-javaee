package com.looseboxes.ratelimiter.web.javaee.weblayer;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.RateLimiterFromProperties;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;
import com.looseboxes.ratelimiter.web.javaee.ResourceClassesSupplierImpl;

import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterWebFeatureImpl extends RateLimiterWebFeature {

    public RateLimiterWebFeatureImpl() {
        this(new RequestToIdConverterImpl(),
                new RateLimiterConfigurerImpl(),
                new RateSupplierImpl(),
                new RateExceededExceptionThrower(),
                new RateLimitPropertiesImpl());
    }

    public RateLimiterWebFeatureImpl(
            RequestToIdConverter<ContainerRequestContext> requestToIdConverter,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimitProperties rateLimitProperties) {
        super(
                rateSupplier,
                rateExceededHandler,
                new RateLimiterFromProperties<>(
                        rateLimitProperties,
                        new RequestToIdConverterRegistryImpl(requestToIdConverter, rateLimiterConfigurer),
                        new RateCacheImpl(),
                        rateSupplier,
                        rateExceededHandler
                ),
                new RequestToIdConverterImpl(),
                new ResourceClassesSupplierImpl(rateLimitProperties),
                rateLimitProperties
        );
    }
}
