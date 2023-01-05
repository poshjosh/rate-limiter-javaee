package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.AbstractResourceLimiterRegistry;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;

import javax.ws.rs.container.ContainerRequestContext;

public final class ResourceLimiterRegistry extends AbstractResourceLimiterRegistry<ContainerRequestContext> {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(ResourceLimiterConfigJaveee.builder().build());
    }

    public static ResourceLimiterRegistry of(
            ResourceLimiterConfig<ContainerRequestContext> resourceLimiterConfig) {
        return new ResourceLimiterRegistry(resourceLimiterConfig);
    }

    private ResourceLimiterRegistry(
            ResourceLimiterConfig<ContainerRequestContext> resourceLimiterConfig) {
        super(resourceLimiterConfig);
    }
}
