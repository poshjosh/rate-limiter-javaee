package com.looseboxes.ratelimiter.web.javaee.weblayer;

import com.looseboxes.ratelimiter.annotation.RateLimit;
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
    @Produces("text/plan")
    public String home() {
        log.debug("home");
        return ApiEndpoints.METHOD_LIMITS_HOME;
    }

    @GET
    @Path(LIMIT_1)
    @Produces("text/plan")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1() {
        log.debug("limit_1");
        return ApiEndpoints.LIMIT_1;
    }

    @GET
    @Path(LIMIT_1_OR_5)
    @Produces("text/plan")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1_or_10() {
        log.debug("limit_1_or_10");
        return ApiEndpoints.LIMIT_1_OR_5;
    }

//    @GET
//    @Path(LIMIT_1_AND_5)
//    @Produces("text/plan")
//    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
//    @RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
//    public String limit_1_and_10(HttpServletRequest request) {
//        log.debug("limit_1_and_10");
//        return ApiEndpoints.LIMIT_1_AND_5;
//    }
}