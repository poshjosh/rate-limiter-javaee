package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;

import javax.ws.rs.ext.Provider;

@Provider
public class TestRateLimiterDynamicFeature extends RateLimiterDynamicFeature {

    public TestRateLimiterDynamicFeature() {
        this(new RateLimitPropertiesImpl(), new RateLimiterConfiguration());
    }

    public TestRateLimiterDynamicFeature(RateLimitProperties properties, RateLimiterConfiguration rateLimiterConfiguration) {
        super(rateLimiterConfiguration.newRateLimiter(properties, null, null), rateLimiterConfiguration.resourceClassesSupplier(properties));
    }
}
