package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;
import com.looseboxes.ratelimiter.web.javaee.ResourceClassesSupplierImpl;

import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterWebFeatureImpl extends RateLimiterWebFeature {

    public RateLimiterWebFeatureImpl() {
        this(
                new RateLimiterConfigurationSourceImpl(
                        new RequestToUriConverter(), new RateCacheImpl(),
                        new RateFactoryImpl(), new RateExceededExceptionThrower(),
                        new RateLimiterProviderImpl(), new RateLimiterConfigurerImpl()
                ),
                new RateLimitPropertiesImpl(),
                new AnnotationProcessorImpl()
        );
    }

    public RateLimiterWebFeatureImpl(
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            RateLimitProperties rateLimitProperties,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        super(
                rateLimitProperties,
                rateLimiterConfigurationSource,
                new ResourceClassesSupplierImpl(rateLimitProperties),
                annotationProcessor
        );
    }
}
