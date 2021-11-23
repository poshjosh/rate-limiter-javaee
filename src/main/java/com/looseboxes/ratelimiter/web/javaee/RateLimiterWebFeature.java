package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.List;

@Provider
public class RateLimiterWebFeature implements DynamicFeature {

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> classes;

    @Inject
    public RateLimiterWebFeature(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimiter<ContainerRequestContext> rateLimiter,
            RequestToIdConverter<ContainerRequestContext> requestToIdConverter,
            ResourceClassesSupplier resourceClassesSupplier,
            RateLimitProperties properties) {

        this.classes = resourceClassesSupplier.get();

        RateLimitHandler<ContainerRequestContext> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateSupplier,
                rateExceededHandler,
                rateLimiter,
                requestToIdConverter,
                annotatedElementIdProviderForClass(),
                annotatedElementIdProviderForMethod(),
                this.classes
        );

        this.containerRequestFilter = rateLimitHandler::handleRequest;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(classes.contains(resourceInfo.getResourceClass())) {
            featureContext.register(containerRequestFilter);
        }
    }

    private AnnotatedElementIdProvider<Method, PathPatterns<String>> annotatedElementIdProviderForMethod() {
        return new MethodIdProvider(annotatedElementIdProviderForClass());
    }

    private AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProviderForClass() {
        return new ClassIdProvider();
    }
}
