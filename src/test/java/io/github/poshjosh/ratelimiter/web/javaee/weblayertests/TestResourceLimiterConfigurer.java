package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.UsageListener;
import io.github.poshjosh.ratelimiter.util.LimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TestResourceLimiterConfigurer implements ResourceLimiterConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(TestResourceLimiterConfigurer.class);
    @Override
    public void configure(Registries registries) {

        registries.registerListener(new UsageListener() {
            @Override
            public void onRejected(Object request, String resourceId, int permits, LimiterConfig<?> config) {
                LOG.warn("onRejected, too many requests for: {}, limits: {}", resourceId, config.getRates());
                throw new WebApplicationException(Response.Status.TOO_MANY_REQUESTS);
            }
        });
    }
}
