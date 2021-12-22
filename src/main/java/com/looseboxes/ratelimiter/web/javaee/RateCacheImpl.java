package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.cache.InMemoryRateCache;

import javax.inject.Singleton;
import java.io.Serializable;

@Singleton
public class RateCacheImpl extends InMemoryRateCache<Serializable, Serializable> {
}
