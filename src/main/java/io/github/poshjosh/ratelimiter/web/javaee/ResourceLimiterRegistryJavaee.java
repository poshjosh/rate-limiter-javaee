package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;

public final class ResourceLimiterRegistryJavaee {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(ResourceLimiterConfigJavaee.builder().build());
    }

    public static ResourceLimiterRegistry of(
            ResourceLimiterConfig resourceLimiterConfig) {
        return ResourceLimiterRegistry.of(resourceLimiterConfig);
    }

    private ResourceLimiterRegistryJavaee() {}
}
