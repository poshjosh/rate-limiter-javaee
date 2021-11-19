package com.looseboxes.ratelimiter.javaee.util;

import com.looseboxes.ratelimiter.rates.Rate;
import com.looseboxes.ratelimiter.rates.Rates;

import java.util.*;

@javax.inject.Singleton
public class RateLimitProperties {

    private String applicationPath = "";

    private List<String> controllerPackages;

    private Boolean disabled;

    private Map<String, RateLimitConfigList> rateLimitConfigs;

    public Map<String, Rate> toRates() {
        final Map<String, Rate> rateMap;
        if(Boolean.TRUE.equals(disabled)) {
            rateMap = Collections.emptyMap();
        }else if(rateLimitConfigs == null || rateLimitConfigs.isEmpty()) {
            rateMap = Collections.emptyMap();
        }else {
            Map<String, Rate> temp = new LinkedHashMap<>(rateLimitConfigs.size());
            rateLimitConfigs.forEach((name, rateLimitConfigList) -> {
                Rate rate = rateLimitConfigList.toRate();
                if(rate != Rate.NONE) {
                    temp.put(name, rate);
                }
            });
            rateMap = Collections.unmodifiableMap(temp);
        }
        return rateMap;
    }

    public Map<String, List<Rate>> toRateLists() {
        final Map<String, List<Rate>> rateMap;
        if(Boolean.TRUE.equals(disabled)) {
            rateMap = Collections.emptyMap();
        }else if(rateLimitConfigs == null || rateLimitConfigs.isEmpty()) {
            rateMap = Collections.emptyMap();
        }else {
            Map<String, List<Rate>> temp = new LinkedHashMap<>(rateLimitConfigs.size());
            rateLimitConfigs.forEach((name, rateLimitConfigList) -> {
                List<Rate> rateList = rateLimitConfigList.toRateList();
                temp.put(name, rateList);
            });
            rateMap = Collections.unmodifiableMap(temp);
        }
        return rateMap;
    }

    public Rates.Logic getLogic(String name) {
        return rateLimitConfigs.get(name).getLogic();
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public List<String> getControllerPackages() {
        return controllerPackages;
    }

    public void setControllerPackages(List<String> controllerPackages) {
        this.controllerPackages = controllerPackages;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Map<String, RateLimitConfigList> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    public void setRateLimitConfigs(Map<String, RateLimitConfigList> rateLimitConfigs) {
        this.rateLimitConfigs = rateLimitConfigs;
    }

    @Override
    public String toString() {
        return "RateLimitProperties{" +
                "applicationPath=" + applicationPath +
                ", controllerPackages=" + controllerPackages +
                ", disabled=" + disabled +
                ", rateLimitConfigs=" + rateLimitConfigs +
                '}';
    }
}
