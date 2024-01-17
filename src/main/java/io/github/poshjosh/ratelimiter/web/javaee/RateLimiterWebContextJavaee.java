package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.javaee.uri.ResourceInfoProviderJavaee;

public final class RateLimiterWebContextJavaee {

    private RateLimiterWebContextJavaee() { }

    public static WebRateLimiterContext.Builder builder() {
        return WebRateLimiterContext.builder()
                .resourceInfoProvider(new ResourceInfoProviderJavaee());
    }
}
