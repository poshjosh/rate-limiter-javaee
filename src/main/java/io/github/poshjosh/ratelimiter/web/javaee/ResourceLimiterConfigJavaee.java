package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.javaee.uri.ResourceInfoProviderJavaee;

public final class ResourceLimiterConfigJavaee {

    private ResourceLimiterConfigJavaee() { }

    public static ResourceLimiterConfig.Builder builder() {
        return ResourceLimiterConfig.builder()
                .resourceInfoProvider(new ResourceInfoProviderJavaee());
    }
}
