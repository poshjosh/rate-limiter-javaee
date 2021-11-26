package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.*;
import com.looseboxes.ratelimiter.util.RateLimitGroupData;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Provider
public class RateLimiterWebFeature implements DynamicFeature {

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> classes;

    @Inject
    public RateLimiterWebFeature(
            RateLimitProperties properties,
            RateLimiter<ContainerRequestContext> rateLimiter,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier) {

        this.classes = resourceClassesSupplier.get();

        RateLimitHandler<ContainerRequestContext> rateLimitHandler = new RateLimitHandler<>(
                properties,
                rateLimiter,
                rateLimiterConfigurationSource,
                resourceClassesSupplier,
                classAnnotationProcessor(),
                methodAnnotationProcessor(),
                classAnnotationCollector(),
                methodAnnotationCollector(),
                classIdProvider(),
                methodIdProvider()
        );

        this.containerRequestFilter = rateLimitHandler::handleRequest;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(classes.contains(resourceInfo.getResourceClass())) {
            featureContext.register(containerRequestFilter);
        }
    }

    private AnnotationProcessor<Class<?>> classAnnotationProcessor() {
        return new ClassAnnotationProcessor();
    }

    private AnnotationProcessor<Method> methodAnnotationProcessor() {
        return new MethodAnnotationProcessor();
    }

    private AnnotationCollector<Class<?>, Map<String, RateLimitGroupData<Class<?>>>> classAnnotationCollector() {
        return new ClassAnnotationCollector();
    }

    private AnnotationCollector<Method, Map<String, RateLimitGroupData<Method>>> methodAnnotationCollector() {
        return new MethodAnnotationCollector();
    }

    private IdProvider<Class<?>, PathPatterns<String>> classIdProvider() {
        return new ClassIdProvider();
    }

    private IdProvider<Method, PathPatterns<String>> methodIdProvider() {
        return new MethodIdProvider(classIdProvider());
    }
}
