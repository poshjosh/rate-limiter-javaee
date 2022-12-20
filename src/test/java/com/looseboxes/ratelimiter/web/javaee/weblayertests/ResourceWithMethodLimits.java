package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import com.looseboxes.ratelimiter.annotation.RateLimitGroup;
import com.looseboxes.ratelimiter.util.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.TimeUnit;

@Path(ResourceWithMethodLimits.Endpoints.ROOT)
public class ResourceWithMethodLimits {

    private final Logger log = LoggerFactory.getLogger(ResourceWithMethodLimits.class);

    private static final String HOME = "/home";
    private static final String LIMIT_1 = "/limit_1";
    private static final String LIMIT_1_OR_5 = "/limit_1_or_5";
    private static final String LIMIT_1_AND_5 = "/limit_1_and_5";

    interface Endpoints{
        String ROOT = "/method-limits";
        String HOME = ROOT + ResourceWithMethodLimits.HOME;
        String LIMIT_1 = ROOT + ResourceWithMethodLimits.LIMIT_1;
        String LIMIT_1_OR_5 = ROOT + ResourceWithMethodLimits.LIMIT_1_OR_5;
        String LIMIT_1_AND_5 = ROOT + ResourceWithMethodLimits.LIMIT_1_AND_5;
    }

    @GET
    @Path(HOME)
    @Produces("text/plain")
    public String home() {
        log.debug("home");
        return ApiEndpoints.METHOD_LIMITS_HOME;
    }

    @GET
    @Path(LIMIT_1)
    @Produces("text/plain")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1() {
        log.debug("limit_1");
        return ApiEndpoints.METHOD_LIMIT_1;
    }

    @GET
    @Path(LIMIT_1_OR_5)
    @Produces("text/plain")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1_or_5() {
        log.debug("limit_1_or_5");
        return ApiEndpoints.METHOD_LIMIT_1_OR_5;
    }

    @GET
    @Path(LIMIT_1_AND_5)
    @Produces("text/plain")
    @RateLimitGroup(logic = Operator.AND)
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1_and_5() {
        log.debug("limit_1_and_5");
        return ApiEndpoints.METHOD_LIMIT_1_AND_5;
    }
}