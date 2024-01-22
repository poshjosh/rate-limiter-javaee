package io.github.poshjosh.ratelimiter.web.javaee.weblayertests;

import io.github.poshjosh.ratelimiter.web.core.RequestInfo;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public final class RequestInfoJavaee implements RequestInfo {
    private final ContainerRequestContext request;

    public RequestInfoJavaee(ContainerRequestContext request) {
        this.request = Objects.requireNonNull(request);
    }

    @Override public String getAuthScheme() {
        String authScheme = request.getSecurityContext().getAuthenticationScheme();
        return authScheme == null ? "" : authScheme;
    }

    @Override public List<Cookie> getCookies() {
        Map<String, javax.ws.rs.core.Cookie> cookieMap = request.getCookies();
        return cookieMap == null || cookieMap.isEmpty() ?
                Collections.emptyList() :
                cookieMap.values().stream()
                        .map(cookie -> Cookie.of(cookie.getName(), cookie.getValue()))
                        .collect(Collectors.toList());
    }

    @Override public List<String> getHeaders(String name) {
        MultivaluedMap<String, String> headers = request.getHeaders();
        return headers == null || headers.isEmpty() ?
                Collections.emptyList() :
                headers.getOrDefault(name, Collections.emptyList());
    }

    @Override public Object getAttribute(String name) {
        return request.getProperty(name);
    }

    @Override public List<String> getParameters(String name) {
        MultivaluedMap<String, String> params = request.getUriInfo().getQueryParameters();
        return params == null || params.isEmpty() ?
                Collections.emptyList() :
                params.getOrDefault(name, Collections.emptyList());
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
        javax.ws.rs.core.Cookie cookie =
                cookieMap == null || cookieMap.isEmpty() ? null : cookieMap.get("JSESSIONID");
        final String sessionId = cookie == null ? null : cookie.getValue();
        return sessionId == null ? "" : sessionId;

    }
    private static String requestUri(ContainerRequestContext request) {
        final String path = request.getUriInfo().getPath();
        return path.startsWith("/") ? path : "/" + path;
    }
}
