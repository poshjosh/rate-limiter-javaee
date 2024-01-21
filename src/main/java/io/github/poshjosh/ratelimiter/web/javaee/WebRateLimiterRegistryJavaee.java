package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;

public final class WebRateLimiterRegistryJavaee {

    public static WebRateLimiterRegistry ofDefaults() {
        return of(WebRateLimiterContextJavaee.builder().build());
    }

    public static WebRateLimiterRegistry of(
            WebRateLimiterContext webRateLimiterContext) {
        return WebRateLimiterRegistry.of(webRateLimiterContext);
    }

    private WebRateLimiterRegistryJavaee() {}
}
