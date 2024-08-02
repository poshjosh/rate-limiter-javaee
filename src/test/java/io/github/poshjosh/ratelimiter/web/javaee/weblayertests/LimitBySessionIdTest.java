package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.annotations.Rate;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionKey;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import java.util.*;

public class LimitBySessionIdTest extends AbstractResourceTest {

    private static final String ROOT = "/limit-by-session-id-test";

    @Path(ROOT)
    @Produces("text/plain")
    public static class Resource { // Has to be public for tests to succeed

        interface Endpoints{
            String BOOKS = ROOT + "/books";
        }

        @Context
        private HttpServletRequest request;

        @GET
        @Path("/books")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String getAll() {
            System.out.println("LimitBySessionTest#getAll");
            return Endpoints.BOOKS;
        }

        @GET
        @Path("/books/{id}")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String getOne(@PathParam("id") String id) {
            System.out.println("LimitBySessionIdTest.Resource#getOne");
            return Endpoints.BOOKS + "/" + id;
        }

        @DELETE
        @Path("/books/{id}")
        @Rate(permits=1, condition=WebExpressionKey.SESSION_ID+" !=")
        public String deleteOne(@PathParam("id") String id) {
            System.out.println("LimitBySessionIdTest.Resource#deleteOne");
            return Endpoints.BOOKS + "/" + id;
        }
    }

    @Override
    protected Set<Class<?>> getResourceOrProviderClasses() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Resource.class)));
    }

    private Cookie sessionCookie;

    @Before
    public void before() {
        sessionCookie = null;
    }

    @Test
    public void givenSameSessionIdAndEndpoint_differentHttpMethodsShouldBeRateLimitedSeparately() {
        final String endpoint = Resource.Endpoints.BOOKS + "/1";
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        sessionCookie = getSessionCookieFromLastResponse();
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, endpoint);
        shouldReturnStatusOfTooManyRequests(HttpMethod.DELETE, endpoint);
    }


    @Test
    public void givenDifferentSessionIdsAndSameEndpoint_shouldNotBeRateLimited() {
        final String endpoint = Resource.Endpoints.BOOKS + "/1";
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
        shouldReturnDefaultResult(HttpMethod.GET, endpoint);
        shouldReturnDefaultResult(HttpMethod.DELETE, endpoint);
    }

    @Test
    public void givenSameSessionIdAndHttpMethod_differentEndpointShouldBeRateLimitedSeparately() {
        shouldReturnDefaultResult(HttpMethod.GET, Resource.Endpoints.BOOKS + "/1");
        sessionCookie = getSessionCookieFromLastResponse();
        shouldReturnDefaultResult(HttpMethod.GET, Resource.Endpoints.BOOKS);
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, Resource.Endpoints.BOOKS + "/1");
        shouldReturnStatusOfTooManyRequests(HttpMethod.GET, Resource.Endpoints.BOOKS);
    }

    @Override
    protected Invocation.Builder buildRequest(String endpoint) {
        if (sessionCookie == null) {
            return super.buildRequest(endpoint);
        }
        return super.buildRequest(endpoint).cookie(sessionCookie);
    }
}
