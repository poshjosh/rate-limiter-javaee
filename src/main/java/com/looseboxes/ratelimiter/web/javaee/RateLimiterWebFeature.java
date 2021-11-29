package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.*;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.*;

@Provider
public class RateLimiterWebFeature implements DynamicFeature {

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    @Inject
    public RateLimiterWebFeature(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {

        this.resourceClasses = resourceClassesSupplier.get();

        RateLimitHandler<ContainerRequestContext> rateLimitHandler = new RateLimitHandler<>(
                properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor
        );

        this.containerRequestFilter = rateLimitHandler::handleRequest;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(resourceClasses.contains(resourceInfo.getResourceClass())) {
            featureContext.register(containerRequestFilter);
        }
    }
}
