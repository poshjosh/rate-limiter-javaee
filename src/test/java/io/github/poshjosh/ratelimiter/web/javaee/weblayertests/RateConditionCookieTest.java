package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Test;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RateConditionCookieTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-cookie-test";

    private static final String cookieName = "text-cookie-name";
    private static final String cookieValue = "text-cookie-value";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String COOKIE_NO_MATCH = ROOT + "/cookie-no-match";
            String COOKIE_MATCH = ROOT + "/cookie-match";
            String COOKIE_MATCH_NAME_ONLY = ROOT + "/cookie-match-name-only";
            String COOKIE_MATCH_OR = ROOT + "/cookie-match-or";
            String COOKIE_NO_MATCH_BAD_OR = ROOT + "/cookie-no-match-bad-or";
        }

        @GET
        @Path("/cookie-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "["+cookieName+"] = invalid-value")
        public String cookieNoMatch() {
            return Endpoints.COOKIE_NO_MATCH;
        }

        @GET
        @Path("/cookie-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "["+cookieName+"] = "+cookieValue)
        public String cookieMatch() {
            return Endpoints.COOKIE_MATCH;
        }

        @GET
        @Path("/cookie-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "[" + cookieName + "] !=")
        public String cookieNegateMatchNameOnly() {
            return Endpoints.COOKIE_MATCH_NAME_ONLY;
        }

        @GET
        @Path("/cookie-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.COOKIE + "[" + cookieName + "] = [invalid-cookie-value | " + cookieValue + "]")
        public String cookieMatchOr() {
            return Endpoints.COOKIE_MATCH_OR;
        }

        @GET
        @Path("/cookie-no-match-bad-or")
        @Rate(1)
        // Badly formatted expression
        @RateCondition(WebExpressionKey.COOKIE + " = " + cookieName + " = [invalid-cookie-value | " + cookieValue + "]}")
        public String cookieNoMatchBarOr() {
            return Endpoints.COOKIE_NO_MATCH_BAD_OR;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    @Test
    public void shouldNotBeRateLimitedWhenCookieNoMatch() {
        final String endpoint = Resource.Endpoints.COOKIE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenCookieMatch() {
        final String endpoint = Resource.Endpoints.COOKIE_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenCookieMatchNameOnly() {
        final String endpoint = Resource.Endpoints.COOKIE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenCookieMatchOr() {
        final String endpoint = Resource.Endpoints.COOKIE_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldReturnServerError_givenBadCondition() {
        final String endpoint = Resource.Endpoints.COOKIE_NO_MATCH_BAD_OR;
        shouldReturnStatus("GET", endpoint, Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected Invocation.Builder buildRequest(String endpoint) {
        return super.buildRequest(endpoint).cookie(cookieName, cookieValue);
    }
}
