package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterRegistry;

public final class RateLimiterRegistryJavaee {

    public static RateLimiterRegistry ofDefaults() {
        return of(RateLimiterContextJavaee.builder().build());
    }

    public static RateLimiterRegistry of(
            RateLimiterContext rateLimiterContext) {
        return RateLimiterRegistry.of(rateLimiterContext);
    }

    private RateLimiterRegistryJavaee() {}
}
