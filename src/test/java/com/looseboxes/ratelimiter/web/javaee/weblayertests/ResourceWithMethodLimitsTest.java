package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithMethodLimits.class);
    }

    @Test
    public void homePageShouldReturnDefaultResult() {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMITS_HOME);
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        Thread.sleep(TimeUnit.SECONDS.toMillis(Constants.DURATION_SECONDS + 1));

        shouldReturnDefaultResult(endpoint);

        shouldReturnStatusOfTooManyRequests(endpoint);
    }

    @Test
    public void andLimitGroupShouldSucceedWhenOneOfManyLimitsIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void andLimitGroupShouldFailWhenAllLimitsAreExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;
        shouldFailWhenMaxLimitIsExceeded(endpoint, Constants.LIMIT_5);
    }
}
