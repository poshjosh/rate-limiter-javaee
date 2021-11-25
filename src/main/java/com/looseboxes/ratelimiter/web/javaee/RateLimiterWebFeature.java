package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.*;
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
            RateLimiter<ContainerRequestContext> rateLimiter,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationRegistry,
            ResourceClassesSupplier resourceClassesSupplier,
            RateLimitProperties properties) {

        this.classes = resourceClassesSupplier.get();

        RateLimiter<String> classRateLimiter = classes.isEmpty() ? RateLimiter.noop() : new ClassPatternsRateLimiter<>(
                this.classes, rateLimiterConfigurationRegistry, classIdProvider());

        RateLimiter<String> methodRateLimiter = classes.isEmpty() ? RateLimiter.noop() : new MethodPatternsRateLimiter<>(
                this.classes, rateLimiterConfigurationRegistry, methodIdProvider());

        RateLimitHandler<ContainerRequestContext> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateLimiter,
                rateLimiterConfigurationRegistry.getDefaultRequestToIdConverter(),
                classRateLimiter, methodRateLimiter
        );

        this.containerRequestFilter = rateLimitHandler::handleRequest;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(classes.contains(resourceInfo.getResourceClass())) {
            featureContext.register(containerRequestFilter);
        }
    }

    private IdProvider<Method, PathPatterns<String>> methodIdProvider() {
        return new MethodIdProvider(classIdProvider());
    }

    private IdProvider<Class<?>, PathPatterns<String>> classIdProvider() {
        return new ClassIdProvider();
    }
}
