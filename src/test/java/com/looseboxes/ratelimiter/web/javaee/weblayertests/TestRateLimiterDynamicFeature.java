package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.ResourceClassesSupplier;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;

import javax.ws.rs.container.ContainerRequestContext;

public class TestRateLimiterDynamicFeature extends RateLimiterDynamicFeature {

    public TestRateLimiterDynamicFeature() {
        this(
                new RateLimitPropertiesImpl(),
                new RateLimiterConfigurationSourceImpl(
                        new RequestToUriConverter(), new RateCacheImpl(),
                        new RateFactoryImpl(), new RateExceededExceptionThrower(),
                        new RateLimiterFactoryImpl(), new RateLimiterConfigurerImpl()
                ),
                new AnnotationProcessorImpl()
        );
    }

    public TestRateLimiterDynamicFeature(
            RateLimitProperties rateLimitProperties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        this(
                rateLimitProperties,
                rateLimiterConfigurationSource,
                new ResourceClassesSupplierImpl(rateLimitProperties),
                annotationProcessor
        );
    }

    public TestRateLimiterDynamicFeature(
            RateLimitProperties rateLimitProperties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {
        super(
                new ContainerRequestContextRateLimiter(
                        rateLimitProperties,
                        rateLimiterConfigurationSource,
                        resourceClassesSupplier,
                        annotationProcessor
                ),
                resourceClassesSupplier
        );
    }}
