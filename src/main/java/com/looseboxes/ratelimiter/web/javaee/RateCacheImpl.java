package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.cache.MapRateCache;

import javax.inject.Singleton;

@Singleton
public class RateCacheImpl extends MapRateCache<Object, Object> {
}
