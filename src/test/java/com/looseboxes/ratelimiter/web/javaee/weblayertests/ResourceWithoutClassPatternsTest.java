package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ResourceWithoutClassPatternsTest extends AbstractResourceTest {

    @Path(Resource.InternalEndpoints.ROOT)
    public static class Resource {

        private final Logger log = LoggerFactory.getLogger(Resource.class);

        private static final String LIMIT_1 = "/limit_1";

        interface InternalEndpoints {
            String ROOT = ""; // No class pattern
            String LIMIT_1 = ROOT + Resource.LIMIT_1;
        }

        interface Endpoints {
            String NO_CLASS_PATTERNS_LIMIT_1 = ApiEndpoints.API + InternalEndpoints.LIMIT_1;
        }

        @GET
        @Path(LIMIT_1)
        @Produces("text/plain")
        @RateLimit(limit = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1() {
            log.debug("limit_1");
            return Resource.Endpoints.NO_CLASS_PATTERNS_LIMIT_1;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(Resource.Endpoints.NO_CLASS_PATTERNS_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.NO_CLASS_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
