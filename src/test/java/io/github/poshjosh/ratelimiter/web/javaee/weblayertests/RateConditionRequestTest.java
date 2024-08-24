package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RateConditionRequestTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-request-test";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        static final String PATH_ONE = "/one";
        static final String PATH_TWO = "/two";
        static final String ENDPOINT_ONE = ROOT + PATH_ONE;
        static final String ENDPOINT_TWO = ROOT + PATH_TWO;

        @GET
        @Path(PATH_ONE)
        @Rate("1/s")
        @RateCondition(WebExpressionKey.REQUEST_URI+" !=")
        public String endpointOne() {
            return ENDPOINT_ONE;
        }

        @GET
        @Path(PATH_TWO)
        @Rate(rate = "1/m", when = WebExpressionKey.HEADER + "[X-SAMPLE-TRIGGER] = true")
        public String endpointTwoGet() {
            return ENDPOINT_TWO;
        }

        @POST
        @Path(PATH_TWO)
        @Rate(rate = "1/m", when = WebExpressionKey.SESSION_ID + " !=")
        public String endpointTwoPost() {
            return ENDPOINT_TWO;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    private Cookie sessionCookie;

    @Before
    public void before() {
        sessionCookie = null;
    }

    @Test
    public void shouldBeRateLimitedWhenRequestUriExists() {
        final String endpoint = Resource.ENDPOINT_ONE;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenSamePathButDifferentMethodsAndRateConditions() {
        shouldReturnDefaultResult(HttpMethod.GET, Resource.ENDPOINT_TWO);
        sessionCookie = getSessionCookieFromLastResponse();
        shouldReturnDefaultResult(HttpMethod.POST, Resource.ENDPOINT_TWO);
    }

    @Override
    protected Invocation.Builder buildRequest(String endpoint) {
        if (sessionCookie == null) {
            return super.buildRequest(endpoint);
        }
        return super.buildRequest(endpoint).cookie(sessionCookie);
    }
}
