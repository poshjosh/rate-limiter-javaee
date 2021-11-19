package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.net.URI;
import java.util.Objects;

public class ContainerRequestFilterImpl implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerRequestFilterImpl.class);

    private final RateLimiter<String>[] rateLimiters;

    @SafeVarargs
    public ContainerRequestFilterImpl(RateLimiter<String>... rateLimiters) {
        this.rateLimiters = Objects.requireNonNull(rateLimiters);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        final URI requestUri = containerRequestContext.getUriInfo().getRequestUri();
        LOG.debug("Request URI: {}, request context: {}", requestUri, containerRequestContext);
        for(RateLimiter<String> rateLimiter : rateLimiters) {
            rateLimiter.record(requestUri.toString());
        }
    }
}
