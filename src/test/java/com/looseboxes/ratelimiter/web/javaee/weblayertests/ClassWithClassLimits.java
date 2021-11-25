package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.TimeUnit;

@Path(ClassWithClassLimits.Endpoints.ROOT)
@RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
@RateLimit(limit = Constants.LIMIT_5, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
public class ClassWithClassLimits {

    interface Endpoints{
        String ROOT = "/class-limits";
        String HOME = ROOT + "/home";
    }

    @GET
    @Path("/home")
    @Produces("text/plain")
    @RateLimit(limit = Constants.LIMIT_5 * 2, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String home() {
        return ApiEndpoints.CLASS_LIMITS_HOME;
    }
}
