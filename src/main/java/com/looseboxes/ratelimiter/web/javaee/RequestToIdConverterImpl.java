package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

@Singleton
public class RequestToIdConverterImpl implements RequestToIdConverter<ContainerRequestContext> {

    @Override
    public Object convert(ContainerRequestContext request) {
        final String path = request.getUriInfo().getPath();
        return path.startsWith("/") ? path : "/" + path;
    }
}
