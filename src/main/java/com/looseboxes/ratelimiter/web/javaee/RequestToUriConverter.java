package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.ws.rs.container.ContainerRequestContext;

public class RequestToUriConverter implements RequestToIdConverter<ContainerRequestContext, String> {

    public RequestToUriConverter() { }

    @Override
    public String convert(ContainerRequestContext request) {
        final String path = request.getUriInfo().getPath();
        return path.startsWith("/") ? path : "/" + path;
    }
}
