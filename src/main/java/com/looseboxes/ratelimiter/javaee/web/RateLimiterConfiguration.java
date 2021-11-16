package com.looseboxes.ratelimiter.javaee.web;

import com.looseboxes.ratelimiter.RateExceededExceptionThrower;
import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.cache.RateCacheInMemory;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;
import org.jvnet.hk2.annotations.Service;

public class RateLimiterConfiguration {

    @Service
    public static final class RateSupplierImpl implements RateSupplier {
        @Override
        public Rate getInitialRate() {
            return new LimitWithinDuration();
        }
    }

    @Service
    public static final class RateCacheImpl extends RateCacheInMemory {  }

    @Service
    public static final class RateExceededHandlerImpl extends RateExceededExceptionThrower {  }
}
