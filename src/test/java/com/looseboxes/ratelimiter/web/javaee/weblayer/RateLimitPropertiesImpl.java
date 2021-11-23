package com.looseboxes.ratelimiter.web.javaee.weblayer;

import com.looseboxes.ratelimiter.rates.Rates;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfigList;
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

    private final Map<String, RateLimitConfigList> rateLimitConfigs;

    public RateLimitPropertiesImpl() {
        this.resourcePackages = Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName());
        this.disabled = Boolean.FALSE;
        this.rateLimitConfigs = Collections.singletonMap(DEFAULT_CONFIG_NAME, getRateLimitConfigList());
    }

    private RateLimitConfigList getRateLimitConfigList() {
        RateLimitConfigList rateLimitConfigList = new RateLimitConfigList();
        rateLimitConfigList.setLimits(getRateLimits());
        rateLimitConfigList.setLogic(Rates.Logic.OR);
        return rateLimitConfigList;
    }

    private List<RateLimitConfig> getRateLimits() {
        RateLimitConfig config = new RateLimitConfig();
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

    @Override public Map<String, RateLimitConfigList> getRateLimitConfigs() {
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
