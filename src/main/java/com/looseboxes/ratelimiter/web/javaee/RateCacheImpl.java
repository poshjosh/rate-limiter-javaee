package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.cache.RateCacheInMemory;

import javax.inject.Singleton;

@Singleton
public class RateCacheImpl extends RateCacheInMemory<Object> {
}
