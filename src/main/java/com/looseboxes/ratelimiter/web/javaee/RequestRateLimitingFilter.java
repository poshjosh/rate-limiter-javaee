package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.Objects;

@javax.annotation.Priority(0)
public class RequestRateLimitingFilter implements ContainerRequestFilter {

    private final RateLimiter<ContainerRequestContext> rateLimiter;

    @javax.inject.Inject
    public RequestRateLimitingFilter(RateLimiter<ContainerRequestContext> rateLimiter) {
        this.rateLimiter = Objects.requireNonNull(rateLimiter);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        this.rateLimiter.increment(requestContext);
    }
}
