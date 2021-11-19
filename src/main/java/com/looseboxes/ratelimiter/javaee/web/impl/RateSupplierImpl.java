package com.looseboxes.ratelimiter.javaee.web.impl;

import com.looseboxes.ratelimiter.RateSupplier;
import com.looseboxes.ratelimiter.rates.LimitWithinDuration;
import com.looseboxes.ratelimiter.rates.Rate;

@javax.inject.Singleton
public class RateSupplierImpl implements RateSupplier {
    @Override
    public Rate getInitialRate() {
        return new LimitWithinDuration();
    }
}
