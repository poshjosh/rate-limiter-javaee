package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.*;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.beans.RateLimitPropertiesImpl;

import javax.ws.rs.container.ContainerRequestContext;

@javax.ws.rs.ext.Provider
public class TestRateLimiterDynamicFeature extends AbstractRateLimiterDynamicFeature {
    public TestRateLimiterDynamicFeature() {
        super(new RateLimitPropertiesImpl(), (RateLimiterConfigurer<ContainerRequestContext>)null);
    }
}
