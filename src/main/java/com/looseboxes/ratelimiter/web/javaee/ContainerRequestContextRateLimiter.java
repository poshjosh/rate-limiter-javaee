package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotationProcessor;
import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurationSource;
import com.looseboxes.ratelimiter.web.core.ResourceClassesSupplier;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class ContainerRequestContextRateLimiter extends WebRequestRateLimiter<ContainerRequestContext> {

    @Inject
    public ContainerRequestContextRateLimiter(
            RateLimitProperties properties,
            RateLimiterConfigurationSource<ContainerRequestContext> rateLimiterConfigurationSource,
            ResourceClassesSupplier resourceClassesSupplier,
            // TODO - This is marked as an ambiguous bean (having multiple matching instances)- Resolve this ambiguity.
            AnnotationProcessor<Class<?>> annotationProcessor) {

        super(properties, rateLimiterConfigurationSource, resourceClassesSupplier.get(), annotationProcessor);
    }
}
