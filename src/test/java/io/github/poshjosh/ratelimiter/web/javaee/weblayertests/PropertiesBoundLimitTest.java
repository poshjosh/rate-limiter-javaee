package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.web.javaee.Assertions;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;

public class PropertiesBoundLimitTest extends AbstractResourceTest{

    @Path(PropertiesBoundLimitTest.Resource._ROOT)
    public static class Resource { // Has to be public for tests to succeed

        private static final String _ROOT = "/properties-bound-limit-test";

        interface Endpoints{
            String HOME = _ROOT + "/home";
        }

        @GET
        @Path("/home")
        @Produces("text/plain")
        public String home() {
            return Resource.Endpoints.HOME;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldHaveAMatcher() {
        Object matcher = getDynamicFeature()
                .getResourceLimiterRegistry()
                .matchers().getOrDefault(TestRateLimitProperties.getResourceBoundToPropertyRates(), null);
        Assertions.assertTrue(matcher != null);
    }

    @Test
    public void shouldBeRateLimited() {
        final String endpoint = Resource.Endpoints.HOME;
        final int limit = TestRateLimitProperties.LIMIT;
        for (int i = 0; i < limit; i++) {
            shouldReturnDefaultResult(endpoint);
        }
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
