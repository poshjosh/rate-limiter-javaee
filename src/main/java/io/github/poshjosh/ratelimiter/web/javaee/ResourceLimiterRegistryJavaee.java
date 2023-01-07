package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;

import javax.ws.rs.container.ContainerRequestContext;

public final class ResourceLimiterRegistryJavaee {

    public static ResourceLimiterRegistry<ContainerRequestContext> ofDefaults() {
        return of(ResourceLimiterConfigJaveee.builder().build());
    }

    public static ResourceLimiterRegistry<ContainerRequestContext> of(
            ResourceLimiterConfig<ContainerRequestContext> resourceLimiterConfig) {
        return ResourceLimiterRegistry.of(resourceLimiterConfig);
    }

    private ResourceLimiterRegistryJavaee() {}
}
