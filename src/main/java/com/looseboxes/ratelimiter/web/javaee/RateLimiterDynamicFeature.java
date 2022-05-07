package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.*;

public class RateLimiterDynamicFeature implements DynamicFeature {

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    @javax.inject.Inject
    public RateLimiterDynamicFeature(RateLimitProperties properties, RateLimiter<ContainerRequestContext> rateLimiter) {

        this.resourceClasses = new ResourceClassesSupplierImpl(properties).get();

        this.containerRequestFilter = new RequestRateLimitingFilter(rateLimiter);
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(isTargetedResource(resourceInfo.getResourceClass())) {
            // final int priority = Integer.MIN_VALUE; // Set rate limiting to highest possible priority
            // final int priority = Priorities.AUTHENTICATION - 1; // Set rate limiting just before authentication
            final int priority = -1; // We can easily see a situation where there are multiple zero priority components
            featureContext.register(containerRequestFilter, priority);
        }
    }

    private boolean isTargetedResource(Class<?> clazz) {
        return resourceClasses.contains(clazz);
    }
}
