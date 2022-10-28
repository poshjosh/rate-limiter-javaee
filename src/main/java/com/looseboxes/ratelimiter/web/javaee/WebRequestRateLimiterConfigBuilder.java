package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.impl.DefaultWebRequestRateLimiterConfigBuilder;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

public final class WebRequestRateLimiterConfigBuilder extends
        DefaultWebRequestRateLimiterConfigBuilder<ContainerRequestContext> {
    public WebRequestRateLimiterConfigBuilder() {
        requestToIdConverter(new RequestToUriConverter());
        classPathPatternsProvider(new ClassPathPatternsProvider());
        methodPathPatternsProvider(new MethodPathPatternsProvider());
        resourceAnnotationTypes(new Class[]{ Path.class });
    }
}
