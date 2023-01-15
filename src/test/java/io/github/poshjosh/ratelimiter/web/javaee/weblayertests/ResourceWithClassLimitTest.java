package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.Rate;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;

public class ResourceWithClassLimitTest extends AbstractResourceTest {

    @Path(Resource._BASE)
    @Rate(permits = 1)
    public static class Resource { // Has to be public for tests to succeed

        private static final String _BASE = "/resource-with-class-limit-test";
        private static final String _HOME = "/home";
        private static final String _BASE_HOME = _BASE + _HOME;

        interface Endpoints{
            String HOME = ApiEndpoints.API + _BASE_HOME;
        }

        @GET
        @Path(_HOME)
        @Produces("text/plain")
        public String home() {
            return _BASE_HOME;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldFailWhenClassLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.HOME;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}