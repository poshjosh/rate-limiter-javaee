package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertFalse;
import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertTrue;

public class DisabledRateLimitingTest extends AbstractResourceTest {

    @Path(Resource._ROOT)
    @Rate(permits = 1, timeUnit = TimeUnit.SECONDS)
    public static class Resource { // Has to be public for tests to succeed

        private static final String _ROOT = "/disabled-rate-limiting-test";
        private static final String _HOME = _ROOT + "/home";

        interface Endpoints {
            String HOME = ApiEndpoints.API + _HOME;
        }

        @GET
        @Path("/home")
        @Produces("text/plain")
        public String home() {
            return _HOME;
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
            final String endpoint = Resource.Endpoints.HOME;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is disabled
        }finally{
            getDynamicFeature().getProperties().setDisabled(originallyDisabled);
        }
    }
}