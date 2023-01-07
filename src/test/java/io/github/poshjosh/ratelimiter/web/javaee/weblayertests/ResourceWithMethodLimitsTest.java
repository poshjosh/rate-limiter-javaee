package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.annotations.RateGroup;
import io.github.poshjosh.ratelimiter.util.Operator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    private static final int LIMIT_5 = 5;

    @Path(Resource.InternalEndpoints.ROOT)
    public static class Resource { // Has to be public for tests to succeed

        private final Logger log = LoggerFactory.getLogger(Resource.class);

        private static final String HOME = "/home";
        private static final String LIMIT_1 = "/limit_1";
        private static final String LIMIT_1_OR_5 = "/limit_1_or_5";
        private static final String LIMIT_1_AND_5 = "/limit_1_and_5";

        interface InternalEndpoints {
            String ROOT = "/resource-with-method-limits-test";
            String HOME = ROOT + Resource.HOME;
            String LIMIT_1 = ROOT + Resource.LIMIT_1;
            String LIMIT_1_OR_5 = ROOT + Resource.LIMIT_1_OR_5;
            String LIMIT_1_AND_5 = ROOT + Resource.LIMIT_1_AND_5;
        }

        interface Endpoints {
            String METHOD_LIMITS_HOME = ApiEndpoints.API + Resource.InternalEndpoints.HOME;
            String METHOD_LIMIT_1 = ApiEndpoints.API + Resource.InternalEndpoints.LIMIT_1;
            String METHOD_LIMIT_1_OR_5 = ApiEndpoints.API + Resource.InternalEndpoints.LIMIT_1_OR_5;
            String METHOD_LIMIT_1_AND_5 = ApiEndpoints.API + Resource.InternalEndpoints.LIMIT_1_AND_5;
        }

        @GET
        @Path(HOME)
        @Produces("text/plain")
        public String home() {
            log.debug("home");
            return Endpoints.METHOD_LIMITS_HOME;
        }

        @GET
        @Path(LIMIT_1)
        @Produces("text/plain")
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1() {
            log.debug("limit_1");
            return Endpoints.METHOD_LIMIT_1;
        }

        @GET
        @Path(LIMIT_1_OR_5)
        @Produces("text/plain")
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        @Rate(permits = LIMIT_5, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1_or_5() {
            log.debug("limit_1_or_5");
            return Endpoints.METHOD_LIMIT_1_OR_5;
        }

        @GET
        @Path(LIMIT_1_AND_5)
        @Produces("text/plain")
        @RateGroup(operator = Operator.AND)
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        @Rate(permits = LIMIT_5, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1_and_5() {
            log.debug("limit_1_and_5");
            return Endpoints.METHOD_LIMIT_1_AND_5;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void homePageShouldReturnDefaultResult() {
        shouldReturnDefaultResult(Resource.Endpoints.METHOD_LIMITS_HOME);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(Resource.Endpoints.METHOD_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void andLimitGroupShouldSucceedWhenOnlyOneLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void andLimitGroupShouldFailWhenAllLimitsAreExceeded() throws Exception {

        final String endpoint = Resource.Endpoints.METHOD_LIMIT_1_AND_5;
        shouldFailWhenMaxLimitIsExceeded(endpoint, LIMIT_5);
    }
}
