package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.web.core.RequestToIdConverter;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.javaee.uri.PathPatternsProviderJavaee;

import javax.ws.rs.container.ContainerRequestContext;

public abstract class ResourceLimiterConfigJaveee
        extends ResourceLimiterConfig<ContainerRequestContext> {

    private static class RequestToIdConverterJavaee
            implements RequestToIdConverter<ContainerRequestContext, String> {
        private RequestToIdConverterJavaee() { }
        @Override
        public String convert(ContainerRequestContext request) {
            final String path = request.getUriInfo().getPath();
            return path.startsWith("/") ? path : "/" + path;
        }
    }

    public static Builder<ContainerRequestContext> builder() {
        return ResourceLimiterConfig.<ContainerRequestContext>builder()
            .pathPatternsProvider(new PathPatternsProviderJavaee())
            .requestToIdConverter(new RequestToIdConverterJavaee());

    }
}
