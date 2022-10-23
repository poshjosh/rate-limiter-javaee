package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotation.RateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.TimeUnit;

@Path(ResourceWithoutClassPatterns.Endpoints.ROOT)
public class ResourceWithoutClassPatterns {

    private final Logger log = LoggerFactory.getLogger(ResourceWithoutClassPatterns.class);

    private static final String LIMIT_1 = "/limit_1";

    interface Endpoints{
        String ROOT = ""; // No class pattern
        String LIMIT_1 = ROOT + ResourceWithoutClassPatterns.LIMIT_1;
    }

    @GET
    @Path(LIMIT_1)
    @Produces("text/plain")
    @RateLimit(limit = Constants.LIMIT_1, duration = Constants.DURATION_SECONDS, timeUnit = TimeUnit.SECONDS)
    public String limit_1() {
        log.debug("limit_1");
        return ApiEndpoints.NO_CLASS_PATTERNS_LIMIT_1;
    }
}