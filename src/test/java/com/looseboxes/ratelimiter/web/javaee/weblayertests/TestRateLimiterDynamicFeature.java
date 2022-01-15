package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;
import com.looseboxes.ratelimiter.web.javaee.RateLimiterConfigurerImpl;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.beans.RateLimitPropertiesImpl;

import javax.ws.rs.container.ContainerRequestContext;

@javax.ws.rs.ext.Provider
public class TestRateLimiterDynamicFeature extends RateLimiterDynamicFeature {

    public TestRateLimiterDynamicFeature() {
        this(new RateLimitPropertiesImpl(), new RateLimiterConfigurerImpl());
    }

    public TestRateLimiterDynamicFeature(
            RateLimitProperties properties,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {
        super(properties, new RateLimiterImpl(properties, rateLimiterConfigurer));
    }
}
