package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.ResourceUsageListener;
import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.ResourceLimitingDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

@javax.ws.rs.ext.Provider
public class TestResourceLimitingDynamicFeature extends ResourceLimitingDynamicFeature {

    private static final Logger log = LoggerFactory.getLogger(TestResourceLimitingDynamicFeature.class);

    private final RateLimitProperties properties;

    @javax.inject.Inject
    public TestResourceLimitingDynamicFeature(RateLimitProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public void configure(Registries<ContainerRequestContext> registries) {

        registries.listeners().register(new ResourceUsageListener() {
            @Override
            public void onRejected(Object resource, Object resourceId, int hits, Object limit) {

                log.warn("Too many requests for: {}, limits: {}", resourceId, limit);

                throw new WebApplicationException("Too may requests for: " + resourceId, Response.Status.TOO_MANY_REQUESTS);
            }
        });
    }

    public TestRateLimitProperties getProperties() {
        return (TestRateLimitProperties) properties;
    }
}
