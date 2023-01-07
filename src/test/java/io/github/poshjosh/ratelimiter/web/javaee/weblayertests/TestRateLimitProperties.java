package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.ElementId;
import io.github.poshjosh.ratelimiter.util.Rate;
import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.util.Operator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@javax.inject.Singleton
public class TestRateLimitProperties implements RateLimitProperties {
    public static final int LIMIT = 3;
    public static final long DURATION_SECONDS = 1;

    private final List<String> resourcePackages;

    private Boolean disabled;

    private final Map<String, Rates> rateLimitConfigs;

    public TestRateLimitProperties() {
        this.resourcePackages = Collections.singletonList(AbstractResourceTest.class.getPackage().getName());
        this.disabled = Boolean.FALSE;
        this.rateLimitConfigs = Collections.singletonMap(getResourceBoundToPropertyRates(), getRateLimitConfigList());
    }

    public static String getResourceBoundToPropertyRates() {
        return ElementId.of(PropertiesBoundLimitTest.Resource.class);
    }

    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }

    private Rate[] getRateLimits() {
        return new Rate[]{Rate.ofSeconds(LIMIT)};
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

    @Override public Map<String, Rates> getRateLimitConfigs() {
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
