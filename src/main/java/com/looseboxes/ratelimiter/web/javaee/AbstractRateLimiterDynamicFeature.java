package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.util.Nullable;
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

public class AbstractRateLimiterDynamicFeature implements DynamicFeature {

    private final Logger log = LoggerFactory.getLogger(AbstractRateLimiterDynamicFeature.class);

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    private final RateLimitProperties properties;

    public AbstractRateLimiterDynamicFeature(RateLimitProperties properties) {
        this(properties, (RateLimiterConfigurer<ContainerRequestContext>)null);
    }

    public AbstractRateLimiterDynamicFeature(RateLimitProperties properties,
                                             @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        this(properties, new RateLimiterImpl(properties, rateLimiterConfigurer));
    }

    public AbstractRateLimiterDynamicFeature(RateLimitProperties properties,
                                             RateLimiter<ContainerRequestContext> rateLimiter) {
        this(properties, new RequestRateLimitingFilter(rateLimiter));
    }

    public AbstractRateLimiterDynamicFeature(RateLimitProperties properties,
                                             RequestRateLimitingFilter requestRateLimitingFilter) {

        this.properties = properties;

        this.resourceClasses = new ResourceClassesSupplierImpl(properties).get();

        this.containerRequestFilter = requestRateLimitingFilter;

        log.info("Completed automatic setup of rate limiting");
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if (log.isTraceEnabled()) {
            log.trace("Enabled: {}, rate limited: {}, class: {}", isEnabled(properties),
                    isTargetedResource(resourceInfo.getResourceClass()), resourceInfo.getResourceClass());
        }
        if(isEnabled(properties) && isTargetedResource(resourceInfo.getResourceClass())) {
            // final int priority = Integer.MIN_VALUE; // Set rate limiting to highest possible priority
            // final int priority = Priorities.AUTHENTICATION - 1; // Set rate limiting just before authentication
            final int priority = -1; // We can easily see a situation where there are multiple zero priority components
            featureContext.register(containerRequestFilter, priority);
        }
    }

    private boolean isEnabled(RateLimitProperties properties) {
        final Boolean disabled = properties.getDisabled();
        return disabled == null || Boolean.FALSE.equals(disabled);
    }

    public boolean isTargetedResource(Class<?> clazz) {
        return resourceClasses.contains(clazz);
    }
}
