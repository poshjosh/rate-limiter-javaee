package com.looseboxes.ratelimiter.javaee.web;

import com.looseboxes.ratelimiter.RateExceededHandler;
import com.looseboxes.ratelimiter.RateLimiter;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.annotation.RateFactoryForClassLevelAnnotation;
import com.looseboxes.ratelimiter.annotation.RateFactoryForMethodLevelAnnotation;
import com.looseboxes.ratelimiter.javaee.util.ClassFilterForAnnotations;
import com.looseboxes.ratelimiter.javaee.util.RateLimitProperties;
import com.looseboxes.ratelimiter.util.ClassFilter;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinderImpl;
import com.looseboxes.ratelimiter.util.RateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Provider
public class RateLimiterWebMvcConfigurer implements DynamicFeature {

    private final Logger log = LoggerFactory.getLogger(RateLimiterWebMvcConfigurer.class);

    private final String applicationPath;

    private final List<String> controllerPackages;

    private final RateSupplier rateSupplier;

    private final RateExceededHandler rateExceededHandler;

    @Inject
    public RateLimiterWebMvcConfigurer(
            RateLimitProperties properties,
            RateSupplier rateSupplier,
            RateExceededHandler rateExceededHandler) {
        this.applicationPath = properties.getApplicationPath();
        this.controllerPackages = properties.getControllerPackages();
        this.rateSupplier = rateSupplier;
        this.rateExceededHandler = rateExceededHandler;
    }


    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        log.debug("Configuring: {}, {}", resourceInfo, featureContext);
        if (controllerPackages != null && !controllerPackages.isEmpty()) {

            RateLimitingInterceptorForRequest rateLimitingInterceptor = new RateLimitingInterceptorForRequest(
                    rateLimiterForClassLevelAnnotation(), rateLimiterForMethodLevelAnnotation()
            );

            log.debug("Registering: {}", rateLimitingInterceptor);

            featureContext.register(rateLimitingInterceptor);
        }
    }

    public RateLimiter<HttpServletRequest> rateLimiterForClassLevelAnnotation() {
        return new RateLimiterForClassLevelAnnotation(
                rateSupplier, rateExceededHandler, rateFactoryForClassLevelAnnotation().getRates());
    }

    public RateLimiter<HttpServletRequest> rateLimiterForMethodLevelAnnotation() {
        return new RateLimiterForMethodLevelAnnotation(
                rateSupplier, rateExceededHandler, rateFactoryForMethodLevelAnnotation().getRates());
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForClassLevelAnnotation() {
        return new RateFactoryForClassLevelAnnotation(getClasses(), annotatedElementIdProviderForClass());
    }

    public RateFactory<AnnotatedRequestMapping> rateFactoryForMethodLevelAnnotation() {
        return new RateFactoryForMethodLevelAnnotation(getClasses(), annotatedElementIdProviderForMethod());
    }

    private List<Class<?>> getClasses() {
        if(controllerPackages == null || controllerPackages.isEmpty()) {
            return Collections.emptyList();
        }
        List<Class<?>> result = new ArrayList<>();
        ClassesInPackageFinder classesInPackageFinder = classesInPackageFinder();
        ClassFilter classFilter = classFilter();
        for(String controllerPackage : controllerPackages) {
            List<Class<?>> packageClasses = classesInPackageFinder.findClasses(controllerPackage, classFilter);
            log.debug("Controller package: {}, classes: {}", controllerPackage);
            result.addAll(packageClasses);
        }
        return result;
    }

    public ClassesInPackageFinder classesInPackageFinder() {
        return new ClassesInPackageFinderImpl();
    }

    public ClassFilter classFilter() {
        return new ClassFilterForAnnotations(Path.class);
    }

    public AnnotatedElementIdProvider<Method, AnnotatedRequestMapping> annotatedElementIdProviderForMethod() {
        return new AnnotatedElementIdProviderForMethodMappings(annotatedElementIdProviderForClass());
    }

    public AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProviderForClass() {
        return new AnnotatedElementIdProviderForRequestMapping(getApplicationMapping());
    }

    private AnnotatedRequestMapping getApplicationMapping() {
        return applicationPath == null ? AnnotatedRequestMapping.NONE : new AnnotatedRequestMappingImpl(applicationPath);
    }
}
