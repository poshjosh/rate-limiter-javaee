package com.looseboxes.ratelimiter.javaee.web;

import com.looseboxes.ratelimiter.RateLimiter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.util.Objects;

public class RateLimitingInterceptorForRequest implements ContainerRequestFilter {

    @Context
    private HttpServletRequest request;

    private final RateLimiter<HttpServletRequest>[] rateLimiters;

    public RateLimitingInterceptorForRequest(RateLimiter<HttpServletRequest>... rateLimiters) {
        this.rateLimiters = Objects.requireNonNull(rateLimiters);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        for(RateLimiter<HttpServletRequest> rateLimiter : rateLimiters) {
            rateLimiter.record(request);
        }
    }
}
