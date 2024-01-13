package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.javaee.uri.ResourceInfoProviderJavaee;

public final class RateLimiterContextJavaee {

    private RateLimiterContextJavaee() { }

    public static RateLimiterContext.Builder builder() {
        return RateLimiterContext.builder()
                .resourceInfoProvider(new ResourceInfoProviderJavaee());
    }
}
