package com.looseboxes.ratelimiter.web.javaee.weblayertests;

import com.looseboxes.ratelimiter.RateExceededException;
import com.looseboxes.ratelimiter.web.javaee.weblayertests.beans.TestRateLimitProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static com.looseboxes.ratelimiter.web.javaee.Assertions.assertThrows;

public abstract class AbstractResourceTest extends JerseyTest {

    private final boolean debugResponse = false;

    private final Logger log = LoggerFactory.getLogger(AbstractResourceTest.class);

    protected abstract Set<Class<?>> getResourceOrProviderClasses();

    private TestRateLimiterDynamicFeature testRateLimiterDynamicFeature;

// Initializing this in the constructor did not work
//    public AbstractResourceTest() {
//        this.testRateLimiterDynamicFeature = new TestRateLimiterDynamicFeature();
//    }

    @Override
    protected Application configure() {
        this.init();
        return ResourceConfig.forApplicationClass(TestApplication.class, getResourceOrProviderClasses())
                .register(this.testRateLimiterDynamicFeature);
    }

    void init() {
        this.testRateLimiterDynamicFeature = new TestRateLimiterDynamicFeature(new TestRateLimitProperties());
    }

    public TestRateLimiterDynamicFeature getDynamicFeature() {
        return this.testRateLimiterDynamicFeature;
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
        shouldReturnResult(endpoint, endpoint);
    }

    <T> void shouldReturnResult(String endpoint, T expectedResult) {
        final WebTarget webTarget = target(endpoint);
        final Invocation.Builder invocationBuilder = webTarget.request("text/plain");
        final Response response = invocationBuilder.get();
        final T responseEntity = (T)response.readEntity(expectedResult.getClass());
        final Response.StatusType statusType = response.getStatusInfo();
        log.info("Request: {}, response: {}, code: {}, type: {}", endpoint, responseEntity,
                statusType.getStatusCode(), statusType.getFamily());
        if(debugResponse) {
            debug(response);
        }
        if (!Response.Status.Family.SUCCESSFUL.equals(statusType.getFamily())) {
            if (Response.Status.TOO_MANY_REQUESTS.equals(statusType)) {
                throw new RateExceededException(statusType.toString());
            }
            throw new RuntimeException(statusType.toString());
        }
        if (!expectedResult.equals(responseEntity)) {
            throw new AssertionError("Expected: " + expectedResult + ", found: " + responseEntity);
        }
    }

    private void debug(Response response) {
        log.debug("Response type: {}, response: {}", response.getClass(), response);
        log.debug("Response status: {}", response.getStatusInfo());
        log.debug("Response links: {}", response.getLinks());
        log.debug("Response headers: {}", response.getHeaders());
        final URI responseLocation = response.getLocation();
        log.debug("Response location: {}", responseLocation);
    }
}
