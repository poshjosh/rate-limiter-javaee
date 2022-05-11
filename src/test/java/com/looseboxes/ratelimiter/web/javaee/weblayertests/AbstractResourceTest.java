package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.web.javaee.RateLimiterDynamicFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertThrows;

public abstract class AbstractResourceTest extends JerseyTest {

    protected abstract Set<Class<?>> getResourceOrProviderClasses();

    @Override
    protected Application configure() {
        return ResourceConfig.forApplicationClass(TestApplication.class, getResourceOrProviderClasses());//.register(RateLimiterDynamicFeature.class);
    }

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) throws Exception {
        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
            }else{
                shouldReturnDefaultResult(endpoint);
            }
        }
    }

    void shouldReturnDefaultResult(String endpoint) {
        final String response = target(endpoint).request().get(String.class);
        if(!endpoint.equals(response)) {
            throw new AssertionError("Expected response: " + endpoint + ", found: " + response);
        }
    }
}
