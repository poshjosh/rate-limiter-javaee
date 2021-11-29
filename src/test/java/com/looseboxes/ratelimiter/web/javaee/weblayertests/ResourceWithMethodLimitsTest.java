package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertThrows;

public class ResourceWithMethodLimitsTest extends AbstractResourceTest {

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(ResourceWithMethodLimits.class);
    }

    @Test
    public void homePageShouldReturnDefaultResult() {
        final String endpoint = ApiEndpoints.METHOD_LIMITS_HOME;
        String response = target(endpoint).request().get(String.class);
        if(!endpoint.equals(response)) {
            throw new AssertionError();
        }
    }

    @Test
    public void shouldSucceedWhenWithinLimit() {
        shouldReturnDefaultResult(ApiEndpoints.METHOD_LIMIT_1);
    }

    @Test
    public void shouldFailWhenLimitIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void orLimitGroupShouldFailWhenOneOfManyLimitsIsExceededAfterADelay() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_OR_5;

        shouldReturnDefaultResult(endpoint);

        Thread.sleep(TimeUnit.SECONDS.toMillis(Constants.DURATION_SECONDS + 1));

        shouldReturnDefaultResult(endpoint);

        assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
    }

    @Test
    public void andLimitGroupShouldSucceedWhenOneOfManyLimitsIsExceeded() {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;

        shouldReturnDefaultResult(endpoint);

        shouldReturnDefaultResult(endpoint);
    }

    @Test
    public void andLimitGroupShouldFailWhenAllOfManyLimitsIsExceeded() throws Exception {

        final String endpoint = ApiEndpoints.METHOD_LIMIT_1_AND_5;
        shouldFailWhenMaxLimitIsExceeded(endpoint, Constants.LIMIT_5);
    }
}
