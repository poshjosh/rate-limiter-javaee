package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.*;
import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.annotation.ClassAnnotationProcessor;
import com.looseboxes.ratelimiter.cache.JavaRateCache;
import com.looseboxes.ratelimiter.cache.MapRateCache;
import com.looseboxes.ratelimiter.cache.RateCache;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinderImpl;
import com.looseboxes.ratelimiter.util.Nullable;
import com.looseboxes.ratelimiter.web.core.*;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.inject.Provider;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;

public class RateLimiterConfiguration {

    public static final String DEFAULT_CACHE_NAME = "com.looseboxes.ratelimiter.web.javaee.cache";

    public RateLimiterConfiguration() { }

    public RateLimiter<ContainerRequestContext> newRateLimiter(
            RateLimitProperties properties,
            @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer,
            @Nullable javax.inject.Provider<CacheManager> cacheManagerProvider) {

        return rateLimiter(
                properties, rateLimiterConfigurationSource(rateLimiterConfigurer, cacheManagerProvider),
                resourceClassesSupplier(properties), annotationProcessor());
    }

    public RateLimiter<ContainerRequestContext> rateLimiter(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            AnnotationProcessor<Class<?>> annotationProcessor) {

        return new WebRequestRateLimiter<>(
                properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor);
    }

    public RequestToIdConverter<ContainerRequestContext, String> requestToIdConverter() {
        return new RequestToUriConverter();
    }

    public RateFactory rateFactory() {
        return  new LimitWithinDurationFactory();
    }

    public RateCache<Object, Object> rateCache(Provider<CacheManager> cacheManagerProvider) {
        CacheManager cacheManager = cacheManagerProvider == null ? null : cacheManagerProvider.get();
        RateCache<Object, Object> rateCache = null;
        if(cacheManager != null) {
            Cache cache = cacheManager.getCache(DEFAULT_CACHE_NAME);
            if(cache != null) {
                rateCache = new JavaRateCache<>(cache);
            }
        }
        if(rateCache == null) {
            rateCache = new MapRateCache<>();
        }
        return rateCache;
    }

    public RateRecordedListener rateRecordedListener() {
        return new RateExceededExceptionThrower();
    }

    public RateLimiterConfigurationSource rateLimiterConfigurationSource(
            @Nullable RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer,
            @Nullable javax.inject.Provider<CacheManager> cacheManagerProvider) {
        return rateLimiterConfigurationSource(
                requestToIdConverter(), rateCache(cacheManagerProvider), rateFactory(),
                rateRecordedListener(), rateLimiterFactory(), rateLimiterConfigurer);
    }

    public RateLimiterConfigurationSource rateLimiterConfigurationSource(
            RequestToIdConverter<ContainerRequestContext, String> requestToUriConverter,
            RateCache<Object, Object> rateCache,
            RateFactory rateFactory,
            RateRecordedListener rateRecordedListener,
            RateLimiterFactory<Object> rateLimiterFactory,
            RateLimiterConfigurer<ContainerRequestContext> rateLimiterConfigurer) {

        return new RateLimiterConfigurationSource<>(
                requestToUriConverter, rateCache, rateFactory, rateRecordedListener, rateLimiterFactory,
                rateLimiterConfigurer, new ClassIdProvider(), new MethodIdProvider());
    }

    public RateLimiterFactory<Object> rateLimiterFactory() {
        return new DefaultRateLimiterFactory<>();
    }

    public ResourceClassesSupplier resourceClassesSupplier(RateLimitProperties properties) {
        return new DefaultResourceClassesSupplier(
                new ClassesInPackageFinderImpl(), properties.getResourcePackages(), Path.class);
    }

    public AnnotationProcessor<Class<?>> annotationProcessor() {
        return new ClassAnnotationProcessor();
    }
}
