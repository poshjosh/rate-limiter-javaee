package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateLimiter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.Objects;

public class RequestRateLimitingFilter implements ContainerRequestFilter {

    private final RateLimiter<ContainerRequestContext> rateLimiter;

    public RequestRateLimitingFilter(RateLimiter<ContainerRequestContext> rateLimiter) {
        this.rateLimiter = Objects.requireNonNull(rateLimiter);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        this.rateLimiter.increment(requestContext);
    }
}
