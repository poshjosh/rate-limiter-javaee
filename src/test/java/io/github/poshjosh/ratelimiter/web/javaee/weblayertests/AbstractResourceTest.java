package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.BandwidthFactory;
import io.github.poshjosh.ratelimiter.web.javaee.Assertions;
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

public abstract class AbstractResourceTest extends JerseyTest {

    static {
        final String bandwidthFactoryClass = BandwidthFactory.AllOrNothing.class.getName();
        System.out.printf("%s [%s] INFO  AbstractResourceTest - Using BandwidthFactory: %s\n",
                java.time.LocalTime.now(), Thread.currentThread().getName(), bandwidthFactoryClass);
        System.setProperty("bandwidth-factory-class", bandwidthFactoryClass);
    }

    private final boolean debugResponse = false;

    private final Logger log = LoggerFactory.getLogger(AbstractResourceTest.class);

    protected abstract Set<Class<?>> getResourceOrProviderClasses();

    private TestResourceLimitingDynamicFeature testRateLimiterDynamicFeature;

// Initializing this in the constructor did not work
//    public AbstractResourceTest() {
//        this.testRateLimiterDynamicFeature = new TestResourceLimitingDynamicFeature();
//    }

    @Override
    protected Application configure() {
        this.init();
        return ResourceConfig.forApplicationClass(TestApplication.class, getResourceOrProviderClasses())
                .register(this.testRateLimiterDynamicFeature);
    }

    void init() {
        this.testRateLimiterDynamicFeature = new TestResourceLimitingDynamicFeature(new TestRateLimitProperties());
    }

    public TestResourceLimitingDynamicFeature getDynamicFeature() {
        return this.testRateLimiterDynamicFeature;
    }

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) throws Exception {
        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                shouldReturnStatusOfTooManyRequests(endpoint);
            }else{
                shouldReturnDefaultResult(endpoint);
            }
        }
    }

    void shouldReturnDefaultResult(String endpoint) {
        shouldReturnResult(endpoint, endpoint);
    }

    void shouldReturnStatusOfTooManyRequests(String endpoint) {
        shouldReturnStatus(endpoint, Response.Status.TOO_MANY_REQUESTS);
    }

    void shouldReturnStatus(String endpoint, Response.Status expectedStatus) {
        final Response response = request(endpoint);
        final Response.StatusType statusType = response.getStatusInfo();
        Assertions.assertEqual(statusType.toEnum(), expectedStatus);
    }

    void shouldReturnResult(String endpoint, Object expectedResult) {
        final Response response = request(endpoint);
        final Response.StatusType statusType = response.getStatusInfo();
        Assertions.assertEqual(statusType.getFamily(), Response.Status.Family.SUCCESSFUL);
        final Object responseEntity = response.readEntity(expectedResult.getClass());
        Assertions.assertEqual(responseEntity, expectedResult);
    }

    private Response request(String endpoint) {
        final Response response = doRequest(endpoint);
        final Response.StatusType statusType = response.getStatusInfo();
        log.info("Request: {}, code: {}, type: {}", endpoint,
                statusType.getStatusCode(), statusType.getFamily());
        debug(response);
        return response;
    }

    protected Response doRequest(String endpoint) {
        final WebTarget webTarget = target(endpoint);
        final Invocation.Builder invocationBuilder = webTarget.request("text/plain");
        return invocationBuilder.get();
    }

    private void debug(Response response) {
        if(!debugResponse) {
            return;
        }
        log.debug("Response type: {}, response: {}", response.getClass(), response);
        log.debug("Response status: {}", response.getStatusInfo());
        log.debug("Response links: {}", response.getLinks());
        log.debug("Response headers: {}", response.getHeaders());
        final URI responseLocation = response.getLocation();
        log.debug("Response location: {}", responseLocation);
    }
}
