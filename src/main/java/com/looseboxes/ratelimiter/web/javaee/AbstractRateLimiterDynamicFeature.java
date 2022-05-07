package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.*;

public abstract class AbstractRateLimiterDynamicFeature implements DynamicFeature {

    private final Logger log = LoggerFactory.getLogger(AbstractRateLimiterDynamicFeature.class);

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    protected AbstractRateLimiterDynamicFeature(RateLimitProperties properties) {
        this(properties, new RateLimiterImpl(properties, (RateLimiterConfigurer<ContainerRequestContext>)null));
    }

    protected AbstractRateLimiterDynamicFeature(RateLimitProperties properties,
                                                RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        this(properties, new RateLimiterImpl(properties, rateLimiterConfigurer));
    }

    protected AbstractRateLimiterDynamicFeature(RateLimitProperties properties, RateLimiter<ContainerRequestContext> rateLimiter) {

        this.resourceClasses = new ResourceClassesSupplierImpl(properties).get();

        this.containerRequestFilter = new RequestRateLimitingFilter(rateLimiter);

        log.info("Completed automatic setup of rate limiting");
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

    protected boolean isTargetedResource(Class<?> clazz) {
        return resourceClasses.contains(clazz);
    }

    protected ContainerRequestFilter getContainerRequestFilter() {
        return containerRequestFilter;
    }
}
