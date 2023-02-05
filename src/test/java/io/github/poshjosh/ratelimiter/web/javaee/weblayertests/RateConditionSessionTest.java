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

public class RateConditionSessionTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-session-test";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String SESSION_ID_EXISTS = ROOT + "/session-id-exists";
        }

        @GET
        @Path("/session-id-exists")
        @Rate(1)
        @RateCondition(WebExpressionKey.SESSION_ID+"!=0") // TODO - Change this to:  !=null
        public String sessionIdExists() {
            return Endpoints.SESSION_ID_EXISTS;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    @Test
    public void shouldNotBeRateLimitedWhenHeaderNoMatch() {
        final String endpoint = Resource.Endpoints.SESSION_ID_EXISTS;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
