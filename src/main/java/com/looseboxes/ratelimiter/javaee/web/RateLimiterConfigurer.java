package com.looseboxes.ratelimiter.javaee.web;

public interface RateLimiterConfigurer {
    void addConverters(RequestToIdConverterRegistry registry);
}
