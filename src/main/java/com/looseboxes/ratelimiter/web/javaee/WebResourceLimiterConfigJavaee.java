package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.impl.WebResourceLimiterConfigBuilder;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

public interface WebResourceLimiterConfigJavaee
        extends WebResourceLimiterConfig<ContainerRequestContext> {

    final class WebResourceLimiterConfigBuilderJavaee extends
            WebResourceLimiterConfigBuilder<ContainerRequestContext> {
        public WebResourceLimiterConfigBuilderJavaee() {
            requestToIdConverter(new RequestToUriConverter());
            classPathPatternsProvider(new ClassPathPatternsProvider());
            methodPathPatternsProvider(new MethodPathPatternsProvider());
            resourceAnnotationTypes(new Class[]{ Path.class });
        }
    }

    static Builder<ContainerRequestContext> builder() {
        return new WebResourceLimiterConfigBuilderJavaee();
    }
}
