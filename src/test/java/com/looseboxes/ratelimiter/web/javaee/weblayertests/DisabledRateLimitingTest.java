package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertFalse;
import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertTrue;

public class DisabledRateLimitingTest extends AbstractResourceTest {

    @Path(Resource.ROOT)
    @RateLimit(limit = 1, duration = 1, timeUnit = TimeUnit.SECONDS)
    public static class Resource { // Has to be public for tests to succeed

        private static final String ROOT = "/disabled-rate-limiting-test";

        private static final class InternalEndpoints {
            private static final String HOME = ROOT + "/home";
        }

        interface Endpoints {
            String CLASS_LIMITS_HOME = ApiEndpoints.API + InternalEndpoints.HOME;
        }

        @GET
        @Path("/home")
        @Produces("text/plain")
        public String home() {
            return InternalEndpoints.HOME;
        }
    }

    private Boolean originallyDisabled;

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Override
    void init() {
        super.init();
        originallyDisabled = getDynamicFeature().getProperties().getDisabled();
        assertFalse(originallyDisabled);
        getDynamicFeature().getProperties().setDisabled(Boolean.TRUE);
    }

    @Test
    public void shouldSucceedWhenDisabled() {
        assertFalse(originallyDisabled);
        assertTrue(getDynamicFeature().getProperties().getDisabled());
        try {
            final String endpoint = Resource.Endpoints.CLASS_LIMITS_HOME;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is originallyDisabled
        }finally{
            getDynamicFeature().getProperties().setDisabled(originallyDisabled);
        }
    }
}