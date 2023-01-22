package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateCondition;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RateConditionLocaleTest extends AbstractResourceTest {

    private static final String ROOT = "/rate-condition-locale-test";

    private static final String acceptLang1 = "en-US";
    private static final String acceptLang2 = "en-UK";
    private static final String noAcceptLang1 = "fr-FR";
    private static final String noAcceptLang2 = "fr-CA";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String LANG_NO_MATCH_OR = ROOT + "/lang-no-match-or";
            String LANG_MATCH_OR = ROOT + "/lang-match-or";
            String LANG_NO_MATCH_AND = ROOT + "/lang-no-match-and";
            String LANG_MATCH_AND = ROOT + "/lang-match-and";
        }

        @GET
        @Path("/lang-no-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + noAcceptLang1 + "|" + noAcceptLang2 + "]")
        public String langNoMatch_or() { return Endpoints.LANG_NO_MATCH_OR; }

        @GET
        @Path("/lang-match-or")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "|" + noAcceptLang1 + "]")
        public String langMatch_or() { return Endpoints.LANG_MATCH_OR; }

        @GET
        @Path("/lang-no-match-and")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "&" + noAcceptLang1 + "]")
        public String langNoMatch_and() { return Endpoints.LANG_NO_MATCH_AND; }

        @GET
        @Path("/lang-match-and")
        @Rate(1)
        @RateCondition(WebExpressionKey.LOCALE + "=[" + acceptLang1 + "&" + acceptLang1 + "]")
        public String langMatch_and() { return Endpoints.LANG_MATCH_AND; }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
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
        return invocationBuilder.get();
    }
}
