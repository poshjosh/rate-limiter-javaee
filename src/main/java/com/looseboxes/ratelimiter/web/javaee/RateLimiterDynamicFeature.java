package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.util.Nullable;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.impl.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.*;

public class RateLimiterDynamicFeature implements DynamicFeature {

    private final Logger log = LoggerFactory.getLogger(RateLimiterDynamicFeature.class);

    private static class RequestRateLimitingFilter implements ContainerRequestFilter {

        private final RateLimiter<ContainerRequestContext> rateLimiter;

        public RequestRateLimitingFilter(RateLimiter<ContainerRequestContext> rateLimiter) {
            this.rateLimiter = Objects.requireNonNull(rateLimiter);
        }

        @Override
        public void filter(ContainerRequestContext requestContext) {
            this.rateLimiter.consume(requestContext);
        }
    }

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    private final RateLimitProperties properties;

    public RateLimiterDynamicFeature(RateLimitProperties properties) {
        this(properties, null);
    }

    public RateLimiterDynamicFeature(
            RateLimitProperties properties,
            @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        this(new WebRequestRateLimiterConfigBuilder().properties(properties).configurer(rateLimiterConfigurer).build());
    }

    public RateLimiterDynamicFeature(
            WebRequestRateLimiterConfig<ContainerRequestContext> webRequestRateLimiterConfig) {

        this.properties = webRequestRateLimiterConfig.getProperties();

        this.resourceClasses = webRequestRateLimiterConfig.getResourceClassesSupplier().get();

        this.containerRequestFilter = new RequestRateLimitingFilter(
                new WebRequestRateLimiter<>(webRequestRateLimiterConfig)
        );

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
