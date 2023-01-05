package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.annotations.Rate;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WithoutDynamicFeatureTest extends AbstractResourceTest {

    @Path(Resource.InternalEndpoints.ROOT)
    public static class Resource { // Has to be public for tests to succeed

        private final Logger log = LoggerFactory.getLogger(Resource.class);

        private static final String _LIMIT_1 = "/limit_1";

        private static final class InternalEndpoints {
            private static final String ROOT = "/without-dynamic-feature-test"; // No class pattern
            private static final String LIMIT_1 = ROOT + _LIMIT_1;
        }

        interface Endpoints {
            String LIMIT_1 = ApiEndpoints.API + Resource.InternalEndpoints.LIMIT_1;
        }

        @GET
        @Path(_LIMIT_1)
        @Produces("text/plain")
        @Rate(permits = 1, duration = 3, timeUnit = TimeUnit.SECONDS)
        public String limit_1() {
            log.debug("limit_1");
            return Resource.Endpoints.LIMIT_1;
        }
    }

    @Override
    protected Application configure() {
        return ResourceConfig.forApplicationClass(TestApplication.class, getResourceOrProviderClasses());
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.singleton(Resource.class);
    }

    @Test
    public void shouldSucceedWhenLimitIsExceeded() {

        final String endpoint = Resource.Endpoints.LIMIT_1;

        shouldReturnDefaultResult(endpoint);

        // Should not fail since we did not register the required DynamicFeature
        shouldReturnDefaultResult(endpoint);
    }
}
