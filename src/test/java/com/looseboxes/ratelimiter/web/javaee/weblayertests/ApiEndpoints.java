package com.looseboxes.ratelimiter.web.javaee.weblayertests;

public interface ApiEndpoints {
    String API = "";

    String CLASS_LIMITS_HOME = API + ResourceWithClassLimits.Endpoints.HOME;

    String METHOD_LIMITS_HOME = API + ResourceWithMethodLimits.Endpoints.HOME;
    String LIMIT_1 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1;
    String LIMIT_1_OR_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_OR_5;
    String LIMIT_1_AND_5 = API + ResourceWithMethodLimits.Endpoints.LIMIT_1_AND_5;
}
