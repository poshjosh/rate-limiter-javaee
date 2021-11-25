package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.util.RateConfig;
import com.looseboxes.ratelimiter.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@javax.inject.Singleton
public class RateLimitPropertiesImpl implements RateLimitProperties {

    public static final String DEFAULT_CONFIG_NAME = "default";

    private final List<String> resourcePackages;

    private final Boolean disabled;

    private final Map<String, RateLimitConfig> rateLimitConfigs;

    public RateLimitPropertiesImpl() {
        this.resourcePackages = Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName());
        this.disabled = Boolean.FALSE;
        this.rateLimitConfigs = Collections.singletonMap(DEFAULT_CONFIG_NAME, getRateLimitConfigList());
    }

    private RateLimitConfig getRateLimitConfigList() {
        RateLimitConfig rateLimitConfig = new RateLimitConfig();
        rateLimitConfig.setLimits(getRateLimits());
        rateLimitConfig.setLogic(Rates.Logic.OR);
        return rateLimitConfig;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = new RateConfig();
        config.setDuration(1);
        config.setLimit(2);
        config.setTimeUnit(TimeUnit.MINUTES);
        return Collections.singletonList(config);
    }

    @Override public List<String> getResourcePackages() {
        return resourcePackages;
    }

    @Override public Boolean getDisabled() {
        return disabled;
    }

    @Override public Map<String, RateLimitConfig> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "RateLimitPropertiesImpl{" +
                "resourcePackages=" + resourcePackages +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
