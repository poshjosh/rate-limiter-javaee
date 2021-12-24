package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.core.*;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.*;

public class RateLimiterDynamicFeature implements DynamicFeature {

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    public RateLimiterDynamicFeature(
            RateLimiter<ContainerRequestContext> rateLimiter,
            ResourceClassesSupplier resourceClassesSupplier) {

        this.resourceClasses = resourceClassesSupplier.get();

        this.containerRequestFilter = rateLimiter::increment;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(resourceClasses.contains(resourceInfo.getResourceClass())) {
            featureContext.register(containerRequestFilter);
        }
    }
}
