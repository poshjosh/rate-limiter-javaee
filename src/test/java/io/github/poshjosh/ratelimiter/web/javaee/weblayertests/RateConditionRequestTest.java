package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RateConditionRequestTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-request-test";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String REQUEST_URI_EXISTS = ROOT + "/request-uri-exists";
        }

        @GET
        @Path("/request-uri-exists")
        @Rate(1)
        @RateCondition(WebExpressionKey.REQUEST_URI+"!=")
        public String requestUriExists() {
            return Endpoints.REQUEST_URI_EXISTS;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    @Test
    public void shouldBeRateLimitedWhenRequestUriExists() {
        final String endpoint = Resource.Endpoints.REQUEST_URI_EXISTS;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
