package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.javaee.uri.ResourceInfoProviderJavaee;

final class WebRateLimiterContextJavaee {

    private WebRateLimiterContextJavaee() { }

    static WebRateLimiterContext.Builder builder() {
        return WebRateLimiterContext.builder()
                .resourceInfoProvider(new ResourceInfoProviderJavaee());
    }
}
