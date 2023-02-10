package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.UsageListener;
import io.github.poshjosh.ratelimiter.util.LimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.javaee.ResourceLimitingDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

//@javax.inject.Singleton - Works in test. However, in actual live server we had to use @Provider
public class TestResourceLimitingDynamicFeature extends ResourceLimitingDynamicFeature {

    private static final Logger log = LoggerFactory.getLogger(TestResourceLimitingDynamicFeature.class);

    public TestResourceLimitingDynamicFeature(RateLimitProperties properties) {
        super(properties);
    }

    @Override
    public void configure(Registries registries) {

        // Option A - here we don't have access to the container request context
        // See Option B below
        //
        registries.registerListener(new UsageListener() {
            @Override
            public void onRejected(Object request, String resourceId, int permits, LimiterConfig<?> config) {

                log.warn("onRejected, too many requests for: {}, limits: {}", resourceId, config.getRates());

                throw new WebApplicationException(Response.Status.TOO_MANY_REQUESTS);
            }
        });
    }

    // Option B - here we have access to the container request context
    // See Option A above
    //
    @Override
    protected void onLimitExceeded(
            HttpServletRequest httpServletRequest, ContainerRequestContext requestContext) {

        //log.warn("onLimitsExceeded, too many requests for: {}", requestContext.getUriInfo());

        //requestContext.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
    }
}
