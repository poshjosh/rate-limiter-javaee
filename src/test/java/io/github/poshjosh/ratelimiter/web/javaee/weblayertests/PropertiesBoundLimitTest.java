package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotation.ElementId;
import io.github.poshjosh.ratelimiter.Operator;
import io.github.poshjosh.ratelimiter.util.Rate;
import io.github.poshjosh.ratelimiter.util.Rates;
import io.github.poshjosh.ratelimiter.web.javaee.Assertions;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.*;

public class PropertiesBoundLimitTest extends AbstractResourceTest{

    private static final int LIMIT = 1;

    @Path(Resource._BASE)
    public static class Resource { // Has to be public for tests to succeed

        private static final String _BASE = "/properties-bound-limit-test";
        private static final String _HOME = "/home";
        private static final String _BASE_HOME = _BASE + _HOME;

        interface Endpoints{
            String HOME = ApiEndpoints.API + _BASE_HOME;
        }

        @GET
        @Path(_HOME)
        @Produces("text/plain")
        public String home() {
            return Resource._BASE_HOME;
        }
        private static String getMethodLimitedViaProperties() {
            try {
                return ElementId.of(Resource.class.getMethod("home"));
            } catch(NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return new HashSet<>(Arrays.asList(Resource.class));
    }

    @Override
    protected TestRateLimitProperties createProperties() {
        TestRateLimitProperties properties = super.createProperties();
        properties.setRateLimitConfigs(
                Collections.singletonMap(Resource.getMethodLimitedViaProperties(), getRateLimitConfigList()));
        return properties;
    }
    private Rates getRateLimitConfigList() {
        return Rates.of(Operator.OR, getRateLimits());
    }
    private Rate[] getRateLimits() {
        return new Rate[]{Rate.ofSeconds(LIMIT)};
    }

    @Test
    public void shouldHaveAMatcher() {
        boolean hasMatching = getDynamicFeature()
                .getResourceLimiterRegistry()
                .hasMatching(Resource.getMethodLimitedViaProperties());
        Assertions.assertTrue(hasMatching);
    }

    @Test
    public void shouldBeRateLimited() {
        final String endpoint = Resource.Endpoints.HOME;
        for (int i = 0; i < LIMIT; i++) {
            shouldReturnDefaultResult(endpoint);
        }
        shouldReturnStatusOfTooManyRequests(endpoint);
    }
}
