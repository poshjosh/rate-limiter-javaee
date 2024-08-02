package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RateConditionHeaderTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-header-test";

    private static final String headerName = "test-header-name";
    private static final String headerValue = "test-header-value";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String HEADER_NO_MATCH = ROOT + "/header-no-match";
            String HEADER_NEGATE_NO_MATCH = ROOT + "/header-negate-no-match";
            String HEADER_MATCH = ROOT + "/header-match";
            String HEADER_MATCH_NAME_ONLY = ROOT + "/header-match-name-only";
            String HEADER_NEGATE_MATCH_NAME_ONLY = ROOT + "/header-negate-match-name-only";
            String HEADER_MATCH_OR = ROOT + "/header-match-or";
            String HEADER_NO_MATCH_BAD_OR = ROOT + "/header-no-match-bad-or";
        }

        @GET
        @Path("/header-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER+" = {invalid-header-name = invalid-header-value}")
        public String headerNoMatch() {
            return Endpoints.HEADER_NO_MATCH;
        }

        @GET
        @Path("/header-negate-no-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER+" != {invalid-header-name = invalid-header-value}")
        public String headerNegateNoMatch() {
            return Endpoints.HEADER_NEGATE_NO_MATCH;
        }

        @GET
        @Path("/header-match")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = {"+headerName+" = "+headerValue+"}")
        public String headerMatch() {
            return Endpoints.HEADER_MATCH;
        }

        @GET
        @Path("/header-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = " + headerName)
        public String headerMatchNameOnly() {
            return Endpoints.HEADER_MATCH_NAME_ONLY;
        }

        @GET
        @Path("/header-negate-match-name-only")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " != " + headerName)
        public String headerNegateMatchNameOnly() {
            return Endpoints.HEADER_NEGATE_MATCH_NAME_ONLY;
        }

        @GET
        @Path("/header-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.HEADER + " = {" + headerName + " = [invalid-cookie-value | " + headerValue + "]}")
        public String headerMatchOr() {
            return Endpoints.HEADER_MATCH_OR;
        }

        @GET
        @Path("/header-no-match-bad-or")
        @Rate(1)
        // Badly formatted, should be {header={name=[A|B]}}, but the second equals sign is missing
        @RateCondition(WebExpressionKey.HEADER + " = {" + headerName + "[invalid-cookie-value | " + headerValue + "]}")
        public String headerNoMatchBadOr() {
            return Endpoints.HEADER_NO_MATCH_BAD_OR;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    @Test
    public void shouldNotBeRateLimitedWhenHeaderNoMatch() {
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenHeaderNegateNoMatch() {
        final String endpoint = Resource.Endpoints.HEADER_NEGATE_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenHeaderMatch() {
        final String endpoint = Resource.Endpoints.HEADER_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenHeaderMatchNameOnly() {
        final String endpoint = Resource.Endpoints.HEADER_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldNotBeRateLimitedWhenHeaderNegateMatchNameOnly() {
        final String endpoint = Resource.Endpoints.HEADER_NEGATE_MATCH_NAME_ONLY;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void shouldBeRateLimitedWhenHeaderMatchOr() {
        final String endpoint = Resource.Endpoints.HEADER_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
    @Test
    public void shouldNotBeRateLimitedWhenHeaderNoMatch_givenBadOr() {
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH_BAD_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Override
    protected Invocation.Builder buildRequest(String endpoint) {
        return super.buildRequest(endpoint).header(headerName, headerValue);
    }
}
