package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.ResourceLimiter;
import com.looseboxes.ratelimiter.annotations.Nullable;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.impl.WebResourceLimiter;
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

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRateLimiterDynamicFeature.class);

    private static class RequestRateLimitingFilter implements ContainerRequestFilter {

        private final ResourceLimiter<ContainerRequestContext> resourceLimiter;

        public RequestRateLimitingFilter(ResourceLimiter<ContainerRequestContext> resourceLimiter) {
            this.resourceLimiter = Objects.requireNonNull(resourceLimiter);
        }

        @Override
        public void filter(ContainerRequestContext requestContext) {
            this.resourceLimiter.tryConsume(requestContext);
            // To better control what happens when a limit is exceeded, implement and expose an
            // instance of com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer
            //if (!this.resourceLimiter.tryConsume(requestContext)) {
            //    requestContext.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
            //}
        }
    }

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    private final RateLimitProperties properties;

    private final WebResourceLimiterConfig<ContainerRequestContext> webResourceLimiterConfig;

    protected AbstractRateLimiterDynamicFeature(RateLimitProperties properties) {
        this(properties, null);
    }

    protected AbstractRateLimiterDynamicFeature(
            RateLimitProperties properties,
            @Nullable ResourceLimiterConfigurer<ContainerRequestContext> resourceLimiterConfigurer) {
    this(
        WebResourceLimiterConfigJavaee.builder()
            .properties(properties)
            .configurer(resourceLimiterConfigurer)
            .build());
    }

    protected AbstractRateLimiterDynamicFeature(
            WebResourceLimiterConfig<ContainerRequestContext> webResourceLimiterConfig) {

        this.webResourceLimiterConfig = webResourceLimiterConfig;

        this.properties = webResourceLimiterConfig.getProperties();

        this.resourceClasses = webResourceLimiterConfig.getResourceClassesSupplier().get();

        this.containerRequestFilter = new RequestRateLimitingFilter(
                new WebResourceLimiter<>(webResourceLimiterConfig)
        );

        LOG.info("Completed automatic setup of rate limiting");
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
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

    public WebResourceLimiterConfig<ContainerRequestContext> getWebRequestRateLimiterConfig() {
        return webResourceLimiterConfig;
    }
}
