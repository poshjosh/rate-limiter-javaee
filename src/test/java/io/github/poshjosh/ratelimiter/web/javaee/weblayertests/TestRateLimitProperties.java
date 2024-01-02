package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.model.Rates;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestRateLimitProperties implements RateLimitProperties {
    private List<Class<?>> resourceClasses;
    private List<String> resourcePackages;

    private Boolean disabled;

    private Map<String, Rates> rateLimitConfigs;

    public TestRateLimitProperties() {
        this.resourceClasses = Collections.emptyList();
        this.resourcePackages = Collections.singletonList(AbstractResourceTest.class.getPackage().getName());
        this.disabled = Boolean.FALSE;
        this.rateLimitConfigs = Collections.emptyMap();
    }

    @Override public List<Class<?>> getResourceClasses() {
        return resourceClasses;
    }

    public void setResourceClasses(List<Class<?>> resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    @Override public List<String> getResourcePackages() {
        return resourcePackages;
    }

    public void setResourcePackages(List<String> resourcePackages) {
        this.resourcePackages = resourcePackages;
    }

    @Override public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override public Map<String, Rates> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    public void setRateLimitConfigs(Map<String, Rates> rateLimitConfigs) {
        this.rateLimitConfigs = rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "TestRateLimitProperties{" +
                "resourcePackages=" + resourcePackages +
                ", resourceClasses=" + resourceClasses +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
