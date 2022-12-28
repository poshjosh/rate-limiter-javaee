package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.WebRequestRateLimiterConfig;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collections;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertTrue;

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
        WebRequestRateLimiterConfig<ContainerRequestContext> config = getDynamicFeature().getWebRequestRateLimiterConfig();
        Object matcher = config.getRegistries().matchers().getOrDefault(
                TestRateLimitProperties.getResourceBoundToPropertyRates(), null);
        assertTrue(matcher != null);
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
