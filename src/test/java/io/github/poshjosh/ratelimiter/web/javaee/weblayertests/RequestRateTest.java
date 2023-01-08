package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.Rate;
import io.github.poshjosh.ratelimiter.util.Operator;
import io.github.poshjosh.ratelimiter.web.core.annotation.RateRequestIf;
import io.github.poshjosh.ratelimiter.web.core.util.MatchType;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class RequestRateTest extends AbstractResourceTest {

    private static final String ROOT = "/request-rate-test";

    private static final String headerName = "test-header-name";
    private static final String headerValue = "test-header-value";

    private static final String acceptLang1 = "en-US";
    private static final String acceptLang2 = "en-UK";
    private static final String noAcceptLang1 = "fr-FR";
    private static final String noAcceptLang2 = "fr-CA";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String HEADER_NO_MATCH = ROOT + "/header-no-match";
            String HEADER_MATCH = ROOT + "/header-match";
            String HEADER_MATCH_NAME_ONLY = ROOT + "/header-match-name-only";
            String LANG_NO_MATCH_OR = ROOT + "/lang-no-match-or";
            String LANG_MATCH_OR = ROOT + "/lang-match-or";
            String LANG_NO_MATCH_AND = ROOT + "/lang-no-match-and";
            String LANG_MATCH_AND = ROOT + "/lang-match-and";
        }

        @GET
        @Path("/header-no-match")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = "invalid-header-name", values = "invalid-header-value")
        public String headerNoMatch() {
            return Resource.Endpoints.HEADER_NO_MATCH;
        }

        @GET
        @Path("/header-match")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = headerName, values = headerValue)
        public String headerMatch() {
            return Endpoints.HEADER_MATCH;
        }

        @GET
        @Path("/header-match-name-only")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.HEADER, name = headerName)
        public String headerMatchNameOnly() {
            return Endpoints.HEADER_MATCH_NAME_ONLY;
        }

        @GET
        @Path("/lang-no-match-or")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {noAcceptLang1, noAcceptLang2}, operator = Operator.OR)
        public String langNoMatch_or() { return Endpoints.LANG_NO_MATCH_OR; }

        @GET
        @Path("/lang-match-or")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, noAcceptLang1}, operator = Operator.OR)
        public String langMatch_or() { return Endpoints.LANG_MATCH_OR; }

        @GET
        @Path("/lang-no-match-and")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, noAcceptLang1})
        public String langNoMatch_and() { return Endpoints.LANG_NO_MATCH_AND; }

        @GET
        @Path("/lang-match-and")
        @Rate(1)
        @RateRequestIf(matchType = MatchType.LOCALE, values = {acceptLang1, acceptLang2})
        public String langMatch_and() { return Endpoints.LANG_MATCH_AND; }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldNotBeRateLimitedWhenHeaderNoMatch() {
        final String endpoint = Resource.Endpoints.HEADER_NO_MATCH;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
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
    public void shouldNotBeRateLimited_givenOr_andNoneOfManyLangMatch() {
        final String endpoint = Resource.Endpoints.LANG_NO_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void shouldBeRateLimited_givenOr_andOneOfManyLangMatch() {
        final String endpoint = Resource.Endpoints.LANG_MATCH_OR;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void shouldNotBeRateLimited_givenAnd_andOnlySomeOfManyLangMatch() {
        final String endpoint = Resource.Endpoints.LANG_NO_MATCH_AND;
        shouldReturnDefaultResult(endpoint);
        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void shouldBeRateLimited_givenAnd_andAllOfManyLangMatch() {
        final String endpoint = Resource.Endpoints.LANG_MATCH_AND;
        shouldReturnDefaultResult(endpoint);
        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Override
    protected Response doRequest(String endpoint) {
        final WebTarget webTarget = target(endpoint);
        Invocation.Builder invocationBuilder = webTarget.request("text/plain");
        invocationBuilder = invocationBuilder.acceptLanguage(acceptLang1 + ',' + acceptLang2);
        invocationBuilder.header(headerName, headerValue);
        return invocationBuilder.get();
    }
}
