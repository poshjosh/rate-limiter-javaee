package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.AbstractResourceLimiterRegistry;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;

import javax.ws.rs.container.ContainerRequestContext;

public final class ResourceLimiterRegistry extends AbstractResourceLimiterRegistry<ContainerRequestContext> {

    public static ResourceLimiterRegistry ofDefaults() {
        return of(WebResourceLimiterConfigJaveee.builder().build());
    }

    public static ResourceLimiterRegistry of(
            WebResourceLimiterConfig<ContainerRequestContext> webResourceLimiterConfig) {
        return new ResourceLimiterRegistry(webResourceLimiterConfig);
    }

    private ResourceLimiterRegistry(
            WebResourceLimiterConfig<ContainerRequestContext> webResourceLimiterConfig) {
        super(webResourceLimiterConfig);
    }
}
