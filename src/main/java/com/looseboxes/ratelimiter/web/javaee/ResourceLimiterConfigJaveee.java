package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.javaee.uri.PathPatternsProviderJavaee;

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
