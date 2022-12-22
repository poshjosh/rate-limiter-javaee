package com.looseboxes.ratelimiter.web.javaee.weblayertests.beans;

import com.looseboxes.ratelimiter.util.Operator;
import com.looseboxes.ratelimiter.web.core.util.RateConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitConfig;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.Constants;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.ResourceWithMethodLimits;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@javax.inject.Singleton
public class TestRateLimitProperties implements RateLimitProperties {

    public static final String DEFAULT_CONFIG_NAME = "default";

    private final List<String> resourcePackages;

    private Boolean disabled;

    private final Map<String, RateLimitConfig> rateLimitConfigs;

    public TestRateLimitProperties() {
        this.resourcePackages = Collections.singletonList(ResourceWithMethodLimits.class.getPackage().getName());
        this.disabled = Boolean.FALSE;
        this.rateLimitConfigs = Collections.singletonMap(DEFAULT_CONFIG_NAME, getRateLimitConfigList());
    }

    private RateLimitConfig getRateLimitConfigList() {
        RateLimitConfig rateLimitConfig = new RateLimitConfig();
        rateLimitConfig.setLimits(getRateLimits());
        rateLimitConfig.setOperator(Operator.OR);
        return rateLimitConfig;
    }

    private List<RateConfig> getRateLimits() {
        RateConfig config = RateConfig.of(
                Constants.OVERALL_LIMIT, Duration.ofSeconds(Constants.OVERALL_DURATION_SECONDS));
        return Collections.singletonList(config);
    }

    @Override public List<String> getResourcePackages() {
        return resourcePackages;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override public Boolean getDisabled() {
        return disabled;
    }

    @Override public Map<String, RateLimitConfig> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "TestRateLimitProperties{" +
                "resourcePackages=" + resourcePackages +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
