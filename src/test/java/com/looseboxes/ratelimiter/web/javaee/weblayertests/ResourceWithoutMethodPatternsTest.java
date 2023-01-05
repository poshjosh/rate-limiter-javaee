package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotations.Rate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ResourceWithoutMethodPatternsTest extends AbstractResourceTest {

    @Path(Resource.InternalEndpoints.ROOT)
    public static class Resource { // Has to be public for tests to succeed

        private final Logger log = LoggerFactory.getLogger(Resource.class);

        private static final String _LIMIT_1 = ""; // No method pattern

        static final class InternalEndpoints {
            static final String ROOT = "/resource-without-method-patterns-test";
            static final String LIMIT_1 = ROOT + _LIMIT_1;
        }

        interface Endpoints {
            String NO_METHOD_PATTERNS_LIMIT_1 = ApiEndpoints.API + InternalEndpoints.LIMIT_1;
        }

        @GET
        @Path(_LIMIT_1)
        @Produces("text/plain")
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1() {
            log.debug("limit_1");
            return Endpoints.NO_METHOD_PATTERNS_LIMIT_1;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(Resource.Endpoints.NO_METHOD_PATTERNS_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.NO_METHOD_PATTERNS_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
