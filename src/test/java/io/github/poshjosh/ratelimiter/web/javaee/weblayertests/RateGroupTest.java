package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateGroup;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.*;

public class RateGroupTest extends AbstractResourceTest{

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return new HashSet<>(Arrays.asList(Resource1.class, Resource2.class));
    }

    @Path(Resource1._BASE)
    @Rate(1)
    @RateGroup("test-rate-limit-group")
    public static class Resource1 { // Has to be public for tests to succeed

        private static final String _BASE = "/rate-group-test/resource1";
        private static final String _HOME = "/home";
        private static final String _BASE_HOME = _BASE + _HOME;

        interface Endpoints{
            String HOME = ApiEndpoints.API + _BASE_HOME;
        }

        @GET
        @Path(_HOME)
        @Produces("text/plain")
        public String home() {
            return Resource1._BASE_HOME;
        }
    }

    @Path(Resource2._BASE)
    public static class Resource2 { // Has to be public for tests to succeed

        private static final String _BASE = "/rate-group-test/resource2";
        private static final String _HOME = "/home";
        private static final String _BASE_HOME = _BASE + _HOME;

        interface Endpoints{
            String HOME = ApiEndpoints.API + _BASE_HOME;
        }

        @GET
        @Path(_HOME)
        @Produces("text/plain")
        @RateGroup("test-rate-limit-group")
        public String home() {
            return Resource2._BASE_HOME;
        }
    }

    @Test
    public void groupMember_whenNoRateDefined_shouldBeRateLimitedByGroupRate() {
        final String endpoint = Resource2.Endpoints.HOME;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
