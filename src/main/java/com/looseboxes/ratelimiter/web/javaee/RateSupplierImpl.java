package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;

import javax.inject.Singleton;

@Singleton
public class RateSupplierImpl implements RateSupplier {

    @Override public Rate getInitialRate() {
        return new LimitWithinDuration();
    }
}
