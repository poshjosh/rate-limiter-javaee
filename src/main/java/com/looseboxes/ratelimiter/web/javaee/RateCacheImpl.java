package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.cache.InMemoryRateCache;

import javax.inject.Singleton;

@Singleton
public class RateCacheImpl extends InMemoryRateCache<Object> {
}
