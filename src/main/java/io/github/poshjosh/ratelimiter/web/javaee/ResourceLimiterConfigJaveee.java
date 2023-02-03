package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.expression.Expression;
import io.github.poshjosh.ratelimiter.web.core.RequestInfo;
import io.github.poshjosh.ratelimiter.web.core.RequestToIdConverter;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.WebExpressionMatcher;
import io.github.poshjosh.ratelimiter.web.javaee.uri.PathPatternsProviderJavaee;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public final class ResourceLimiterConfigJaveee{

    private static class RequestInfoJavaee implements RequestInfo {
        private final ContainerRequestContext request;
        private RequestInfoJavaee(ContainerRequestContext request) {
            this.request = Objects.requireNonNull(request);
        }
        @Override public String getAuthScheme() {
            String authScheme = request.getSecurityContext().getAuthenticationScheme();
            return authScheme == null ? "" : authScheme;
        }
        @Override public List<Cookie> getCookies() {
            Map<String, javax.ws.rs.core.Cookie> cookieMap = request.getCookies();
            return cookieMap == null || cookieMap.isEmpty() ? Collections.emptyList() :
                    cookieMap.values().stream()
                            .map(cookie -> Cookie.of(cookie.getName(), cookie.getValue()))
                            .collect(Collectors.toList());
        }
        @Override public List<String> getHeaders(String name) {
            MultivaluedMap<String, String> headers = request.getHeaders();
            return headers == null || headers.isEmpty() ?
                    Collections.emptyList() : headers.getOrDefault(name, Collections.emptyList());
        }
        @Override public Object getAttribute(String name) {
            return request.getProperty(name);
        }
        @Override public List<String> getParameters(String name) {
            MultivaluedMap<String, String> params = request.getUriInfo().getQueryParameters();
            return params == null || params.isEmpty()
                    ? Collections.emptyList() : params.getOrDefault(name, Collections.emptyList());
        }
        @Override public String getRemoteAddr() {
            // TODO Issue #2 - Implement this method
            // ContainerRequestContext does not provide access to the remote address
            // To access the remote address we need to use @javax.ws.rs.core.Context to inject
            // a javax.servlet.http.HttpServletRequest and call its getRemoteAddr() method
            // However, within this method, we are in a static context, which makes injection
            // extra-ordinary
            throw notYetImplemented();
        }
        private static RuntimeException notYetImplemented() {
            return new UnsupportedOperationException(
                "Not yet implemented. See https://github.com/poshjosh/rate-limiter-javaee/issues/2");
        }
        @Override public List<Locale> getLocales() {
            List<Locale> locales = request.getAcceptableLanguages();
            return locales == null || locales.isEmpty() ? Collections.emptyList() : locales;
        }
        @Override public boolean isUserInRole(String role) {
            return request.getSecurityContext().isUserInRole(role);
        }
        @Override public Principal getUserPrincipal() {
            return request.getSecurityContext().getUserPrincipal();
        }
        @Override public String getRequestUri() {
            return requestUri(request);
        }
        @Override public String getSessionId() {
            Map<String, javax.ws.rs.core.Cookie> cookieMap = request.getCookies();
            javax.ws.rs.core.Cookie cookie = cookieMap == null || cookieMap.isEmpty()
                    ? null : cookieMap.get("JSESSIONID");
            final String sessionId = cookie == null ? null : cookie.getValue();
            return sessionId == null ? "" : sessionId;

        }
        private static String requestUri(ContainerRequestContext request) {
            final String path = request.getUriInfo().getPath();
            return path.startsWith("/") ? path : "/" + path;
        }
    }

    private static class RequestToIdConverterJavaee
            implements RequestToIdConverter<ContainerRequestContext, String> {
        @Override public String toId(ContainerRequestContext request) {
            return RequestInfoJavaee.requestUri(request);
        }
    }

    private static class WebExpressionMatcherJavaee
            extends WebExpressionMatcher<ContainerRequestContext> {
        private WebExpressionMatcherJavaee() { }
        @Override protected RequestInfo info(ContainerRequestContext request) {
            return new RequestInfoJavaee(request);
        }
        @Override public boolean isSupported(Expression<String> expression) {
            if (REMOTE_ADDRESS.equals(expression.getLeft())){ // Not yet implemented
                return false;
            }
            return super.isSupported(expression);
        }
    }

    private ResourceLimiterConfigJaveee() { }

    public static ResourceLimiterConfig.Builder<ContainerRequestContext> builder() {
        return ResourceLimiterConfig.builder(ContainerRequestContext.class)
            .pathPatternsProvider(new PathPatternsProviderJavaee())
            .requestToIdConverter(new RequestToIdConverterJavaee())
            .expressionMatcher(new WebExpressionMatcherJavaee());

    }
}
