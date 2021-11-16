package com.looseboxes.ratelimiter.javaee.web;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;

import javax.ws.rs.Path;
import java.util.Objects;

public class AnnotatedElementIdProviderForRequestMapping implements AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> {

    private final AnnotatedRequestMapping applicationMapping;

    public AnnotatedElementIdProviderForRequestMapping(AnnotatedRequestMapping applicationMapping) {
        this.applicationMapping = Objects.requireNonNull(applicationMapping);
    }

    @Override
    public AnnotatedRequestMapping getId(Class<?> source) {

        final Path pathAnnotation = source.getAnnotation(Path.class);

        if(pathAnnotation == null) {
            return AnnotatedRequestMapping.NONE;
        }

        final String path = pathAnnotation.value();

        if(path == null || path.isEmpty()) {

            return AnnotatedRequestMapping.NONE;
        }

        return applicationMapping.combine(path);
    }
}
