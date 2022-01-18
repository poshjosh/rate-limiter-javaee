package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.TimeUnit;

@Path(ResourceWithoutMethodPatterns.Endpoints.ROOT)
public class ResourceWithoutMethodPatterns {

    private final Logger log = LoggerFactory.getLogger(ResourceWithoutMethodPatterns.class);

    private static final String LIMIT_1 = ""; // No method pattern

    interface Endpoints{
        String ROOT = "/no-method-patterns";
        String LIMIT_1 = ROOT + ResourceWithoutMethodPatterns.LIMIT_1;
    }

    @GET
    @Path(LIMIT_1)
    @Produces("text/plan")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1() {
        log.debug("limit_1");
        return ApiEndpoints.NO_METHOD_PATTERNS_LIMIT_1;
    }
}