package com.looseboxes.ratelimiter.javaee.util;

import java.util.List;
import java.util.Map;

public interface RateLimitProperties {

    String getApplicationPath();

    List<String> getControllerPackages();

    Boolean getDisabled();

    Map<String, RateLimitConfigList> getRateLimitConfigs();
}
