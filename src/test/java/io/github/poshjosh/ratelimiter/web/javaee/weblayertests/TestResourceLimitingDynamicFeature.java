package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.UsageListener;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.javaee.ResourceLimitingDynamicFeature;
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

        // Option A - here we don't have access to the container request context
        // See Option B below
        //
        registries.listeners().register(new UsageListener() {
            @Override
            public void onRejected(Object resource, int hits, Object limit) {

                log.warn("onRejected, too many requests for: {}, limits: {}", resource, limit);

                throw new WebApplicationException(Response.Status.TOO_MANY_REQUESTS);
            }
        });
    }

    // Option B - here we have access to the container request context
    // See Option A above
    //
    @Override
    protected void onLimitExceeded(ContainerRequestContext requestContext) {

        //log.warn("onLimitsExceeded, too many requests for: {}", requestContext.getUriInfo());

        //requestContext.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
    }

    public TestRateLimitProperties getProperties() {
        return (TestRateLimitProperties) properties;
    }
}
