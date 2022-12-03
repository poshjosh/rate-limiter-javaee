package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfigBuilder;
import com.looseboxes.ratelimiter.web.core.impl.DefaultWebRequestRateLimiterConfigBuilder;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

public interface WebRequestRateLimiterConfigJavaee
        extends WebRequestRateLimiterConfig<ContainerRequestContext> {

    final class WebRequestRateLimiterConfigBuilderJavaee extends
            DefaultWebRequestRateLimiterConfigBuilder<ContainerRequestContext> {
        public WebRequestRateLimiterConfigBuilderJavaee() {
            requestToIdConverter(new RequestToUriConverter());
            classPathPatternsProvider(new ClassPathPatternsProvider());
            methodPathPatternsProvider(new MethodPathPatternsProvider());
            resourceAnnotationTypes(new Class[]{ Path.class });
        }
    }

    static WebRequestRateLimiterConfigBuilder<ContainerRequestContext> builder() {
        return new WebRequestRateLimiterConfigBuilderJavaee();
    }
}
