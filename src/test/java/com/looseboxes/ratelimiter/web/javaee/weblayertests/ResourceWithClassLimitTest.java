package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotations.RateLimit;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ResourceWithClassLimitTest extends AbstractResourceTest {

    @Path(Resource.ROOT)
    @RateLimit(limit = 1, duration = 1, timeUnit = TimeUnit.SECONDS)
    public static class Resource { // Has to be public for tests to succeed

        private static final String ROOT = "/resource-with-class-limit-test";

        interface Endpoints{
            String HOME = ROOT + "/home";
        }

        @GET
        @Path("/home")
        @Produces("text/plain")
        public String home() {
            return Endpoints.HOME;
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