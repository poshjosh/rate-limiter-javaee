package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.util.RateConfigList;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@javax.inject.Singleton
public class RateLimitPropertiesImpl implements RateLimitProperties {

    @Override
    public List<String> getResourcePackages() {
        throw new UnsupportedOperationException("Subclasses should implement this method!");
    }

    @Override
    public Map<String, RateConfigList> getRateLimitConfigs() {
        return Collections.emptyMap();
    }
}
