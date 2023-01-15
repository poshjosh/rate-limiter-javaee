package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;

import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertFalse;
import static io.github.poshjosh.ratelimiter.web.javaee.Assertions.assertTrue;

public class DisabledRateLimitingTest extends AbstractResourceTest {

    @Path(Resource._BASE)
    @Rate(permits = 1)
    public static class Resource { // Has to be public for tests to succeed

        private static final String _BASE = "/disabled-rate-limiting-test";
        private static final String _HOME = "/home";
        private static final String _BASE_HOME = _BASE + _HOME;

        interface Endpoints {
            String HOME = ApiEndpoints.API + _BASE_HOME;
        }

        @GET
        @Path(_HOME)
        @Produces("text/plain")
        public String home() { return _BASE_HOME; }
    }

    private Boolean originallyDisabled;

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Override
    protected TestRateLimitProperties createProperties() {
        TestRateLimitProperties properties = super.createProperties();
        originallyDisabled = properties.getDisabled();
        assertFalse(originallyDisabled);
        properties.setDisabled(Boolean.TRUE);
        return properties;
    }

    @Test
    public void shouldSucceedWhenDisabled() {
        assertFalse(originallyDisabled);
        assertTrue(getProperties().getDisabled());
        try {
            final String endpoint = Resource.Endpoints.HOME;
            shouldReturnDefaultResult(endpoint); // 1 of 1
            shouldReturnDefaultResult(endpoint); // 2 of 1 - Should succeed if rate limiting is disabled
        }finally{
            getProperties().setDisabled(originallyDisabled);
        }
    }
}