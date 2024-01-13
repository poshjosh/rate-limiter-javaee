package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.bandwidths.BandwidthFactory;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.javaee.Assertions;
import io.github.poshjosh.ratelimiter.web.javaee.RateLimitingDynamicFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.*;

public abstract class AbstractResourceTest extends JerseyTest {

    static {
        final String bandwidthFactoryClass = BandwidthFactory.AllOrNothing.class.getName();
        System.out.printf("%s [%s] INFO  AbstractResourceTest - Using BandwidthFactory: %s\n",
                java.time.LocalTime.now(), Thread.currentThread().getName(), bandwidthFactoryClass);
        System.setProperty("bandwidth-factory-class", bandwidthFactoryClass);
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceTest.class);

    private static TestRateLimitProperties testRateLimitProperties;
    private static RateLimiterRegistry rateLimiterRegistry;

    @Provider
    public static class TestDynamicFeature extends RateLimitingDynamicFeature {
        @Inject
        public TestDynamicFeature() {
            super(Objects.requireNonNull(testRateLimitProperties));
            rateLimiterRegistry = getRateLimiterRegistry();
        }

        @Override
        protected void onLimitExceeded(HttpServletRequest request,
                ContainerRequestContext requestContext) {
            super.onLimitExceeded(request, requestContext);
            LOG.warn("onRejected, too many requests for: {}", request);
            throw new WebApplicationException(Response.Status.TOO_MANY_REQUESTS);
        }
    }

    private final boolean debugResponse = true;

    protected abstract Set<Class<?>> getResourceOrProviderClasses();

    @Override
    protected ResourceConfig configure() {
        testRateLimitProperties = null;
        TestRateLimitProperties properties = createProperties();
        properties.setResourcePackages(Collections.emptyList());
        properties.setResourceClasses(new ArrayList<>(getResourceOrProviderClasses()));
        testRateLimitProperties = properties;
        Set<Class<?>> classes = new HashSet<>(getResourceOrProviderClasses());
        classes.add(TestDynamicFeature.class);
        return ResourceConfig
                .forApplicationClass(TestApplication.class, classes)
                .register(new ExceptionMapper<Throwable>() {
                    @Override
                    public Response toResponse(Throwable ex) {
                        if (debugResponse) {
                            ex.printStackTrace();
                        }
                        if (ex instanceof WebApplicationException) {
                            return ((WebApplicationException)ex).getResponse();
                        }
                        if (!debugResponse) {
                            ex.printStackTrace();;
                        }
                        return Response.serverError().entity(ex.getMessage()).build();
                    }
                });
    }

    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    public DeploymentContext configureDeployment() {
        return ServletDeploymentContext.forServlet(new ServletContainer(configure())).build();
    }

    protected TestRateLimitProperties createProperties() {
        return new TestRateLimitProperties();
    }

    protected TestRateLimitProperties getProperties() {
        return testRateLimitProperties;
    }

    public RateLimiterRegistry getRateLimiterRegistry() {
        return rateLimiterRegistry;
    }

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) {
        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                shouldReturnStatusOfTooManyRequests(endpoint);
            }else{
                shouldReturnDefaultResult(endpoint);
            }
        }
    }

    void shouldReturnDefaultResult(String endpoint) {
        shouldReturnDefaultResult("GET", endpoint);
    }

    void shouldReturnDefaultResult(String method, String endpoint) {
        shouldReturnResult(method, endpoint, endpoint);
    }

    void shouldReturnStatusOfTooManyRequests(String endpoint) {
        shouldReturnStatusOfTooManyRequests("GET", endpoint);
    }

    void shouldReturnStatusOfTooManyRequests(String method, String endpoint) {
        shouldReturnStatus(method, endpoint, Response.Status.TOO_MANY_REQUESTS);
    }

    void shouldReturnStatus(String method, String endpoint, Response.Status expectedStatus) {
        final Response response = request(method, endpoint);
        final Response.StatusType statusType = response.getStatusInfo();
        Assertions.assertEqual(statusType.toEnum(), expectedStatus);
    }

    void shouldReturnResult(String method, String endpoint, Object expectedResult) {
        final Response response = request(method, endpoint);
        final Response.StatusType statusType = response.getStatusInfo();
        Assertions.assertEqual(statusType.getFamily(), Response.Status.Family.SUCCESSFUL);
        final Object responseEntity = response.readEntity(expectedResult.getClass());
        Assertions.assertEqual(responseEntity, expectedResult);
    }

    private NewCookie sessionCookieFromLastResponse;

    private Response request(String method, String endpoint) {
        final Response response = sendRequest(method, endpoint);
        sessionCookieFromLastResponse = response.getCookies().get("JSESSIONID");
        final Response.StatusType statusType = response.getStatusInfo();
        LOG.info("Request: {}, code: {}, type: {}", endpoint,
                statusType.getStatusCode(), statusType.getFamily());
        debug(response);
        return response;
    }

    public NewCookie getSessionCookieFromLastResponse() {
        return sessionCookieFromLastResponse;
    }

    private Response sendRequest(String method, String endpoint) {
        final Invocation.Builder invocationBuilder = buildRequest(endpoint);
        return invocationBuilder.build(method).invoke();
    }

    protected Invocation.Builder buildRequest(String endpoint) {
        final WebTarget webTarget = target(endpoint);
        return webTarget.request("text/plain");
    }

    private void debug(Response response) {
        if(!debugResponse) {
            return;
        }
        LOG.debug("Response type: {}, response: {}", response.getClass(), response);
        LOG.debug("Response status: {}", response.getStatusInfo());
        LOG.debug("Response cookies: {}", response.getCookies().get("JSESSIONID"));
        LOG.debug("Response links: {}", response.getLinks());
        LOG.debug("Response headers: {}", response.getHeaders());
        LOG.debug("Response location: {}", response.getLocation());
        LOG.debug("Response entity: {}", response.getEntity());
    }
}
