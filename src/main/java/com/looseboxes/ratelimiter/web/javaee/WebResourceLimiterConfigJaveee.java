package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.javaee.uri.JavaeePathPatternsProvider;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

public abstract class WebResourceLimiterConfigJaveee
        extends WebResourceLimiterConfig<ContainerRequestContext> {

    public static Builder<ContainerRequestContext> builder() {
        return WebResourceLimiterConfig.<ContainerRequestContext>builder()
            .requestToIdConverter(new RequestToUriConverter())
            .pathPatternsProvider(new JavaeePathPatternsProvider())
            .resourceAnnotationTypes(new Class[]{ Path.class });
    }
}
