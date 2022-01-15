package com.looseboxes.ratelimiter.web.javaee.weblayertests;

public interface ApiEndpoints {
    String API = "";

    String CLASS_LIMITS_HOME = API + ResourceWithClassLimits.Endpoints.HOME;

    String METHOD_LIMITS_HOME = API + ResourceWithMethodLimits.Endpoints.HOME;
    String METHOD_LIMIT_1 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1;
    String METHOD_LIMIT_1_OR_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_OR_5;
    String METHOD_LIMIT_1_AND_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_AND_5;
    String NO_CLASS_PATTERNS_LIMITS_HOME = API + ResourceWithoutClassPatterns.Endpoints.HOME;
    String NO_CLASS_PATTERNS_LIMIT_1 = API + ResourceWithoutClassPatterns.Endpoints.LIMIT_1;
}
