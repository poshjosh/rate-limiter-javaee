package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RequestToUriConverter implements RequestToIdConverter<ContainerRequestContext, String> {

    @Override
    public String convert(ContainerRequestContext request) {
        final String path = request.getUriInfo().getPath();
        return path.startsWith("/") ? path : "/" + path;
    }
}
