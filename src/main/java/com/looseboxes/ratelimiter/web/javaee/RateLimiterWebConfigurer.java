package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.util.*;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Provider
public class RateLimiterWebConfigurer implements DynamicFeature {

    private final RateLimiter<String> rateLimiterForClassLevelAnnotations;

    private final RateLimiter<String> rateLimiterForMethodLevelAnnotations;

    @Inject
    public RateLimiterWebConfigurer(
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler,
            RateLimitProperties properties) {

        final List<Class<?>> classes = getClasses(properties);
        final String applicationPath = getApplicationPath(properties);

        this.rateLimiterForClassLevelAnnotations = classes.isEmpty() ? RateLimiter.noop() :
                new RateLimiterFromClassLevelAnnotations<>(
                        rateSupplier, rateExceededHandler, classes,
                        annotatedElementIdProviderForClass(applicationPath)
                );

        this.rateLimiterForMethodLevelAnnotations = classes.isEmpty() ? RateLimiter.noop() :
                new RateLimiterFromMethodLevelAnnotations<>(
                        rateSupplier, rateExceededHandler, classes,
                        annotatedElementIdProviderForMethod(applicationPath)
                );
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {

        ContainerRequestFilter containerRequestFilter = new ContainerRequestFilterImpl(
                rateLimiterForClassLevelAnnotations, rateLimiterForMethodLevelAnnotations
        );

        featureContext.register(containerRequestFilter);
    }

    private String getApplicationPath(RateLimitProperties properties) {
        final String value = properties.getApplicationPath();
        return value == null ? "" : value;
    }

    private List<Class<?>> getClasses(RateLimitProperties properties) {
        final List<String> resourcePackages = properties.getResourcePackages();
        final List<Class<?>> classes;
        if(resourcePackages == null || resourcePackages.isEmpty()) {
            classes = Collections.emptyList();
        }else{
            classes = new ClassesInPackageFinderImpl().findClasses(resourcePackages, new ClassFilterForAnnotations(Path.class));
        }
        return classes;
    }

    private AnnotatedElementIdProvider<Method, PathPatterns<String>> annotatedElementIdProviderForMethod(String applicationPath) {
        return new AnnotatedElementIdProviderForMethod(annotatedElementIdProviderForClass(applicationPath));
    }

    private AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProviderForClass(String applicationPath) {
        return new AnnotatedElementIdProviderForClass(getApplicationMapping(applicationPath));
    }

    private PathPatterns<String> getApplicationMapping(String applicationPath) {
        return applicationPath == null ? PathPatterns.none() : new BasicClassPathPatterns(applicationPath);
    }
}
