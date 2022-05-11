package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RateLimiterConfigurer;

import javax.ws.rs.container.ContainerRequestContext;

@javax.inject.Singleton
public class RateLimiterConfigurerImpl implements RateLimiterConfigurer<ContainerRequestContext> { }
