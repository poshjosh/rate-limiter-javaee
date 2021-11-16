package com.looseboxes.ratelimiter.javaee.web;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotatedElementIdProviderForMethodMappings implements AnnotatedElementIdProvider<Method, AnnotatedRequestMapping> {

    private final AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProvider;

    public AnnotatedElementIdProviderForMethodMappings(AnnotatedElementIdProvider<Class<?>, AnnotatedRequestMapping> annotatedElementIdProvider) {
        this.annotatedElementIdProvider = Objects.requireNonNull(annotatedElementIdProvider);
    }

    @Override
    public AnnotatedRequestMapping getId(Method method) {

        final AnnotatedRequestMapping annotatedRequestMapping = annotatedElementIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(Path.class) != null) {
            AnnotatedRequestMapping mapping = buildPathPatterns(annotatedRequestMapping, method.getAnnotation(Path.class).value());
            return mapping;
        }

        return annotatedRequestMapping;
    }

    private AnnotatedRequestMapping buildPathPatterns(AnnotatedRequestMapping annotatedRequestMapping, String subPathPattern) {
        if(subPathPattern == null || subPathPattern.isEmpty()) {
            return AnnotatedRequestMapping.NONE;
        }
        return annotatedRequestMapping.combine(subPathPattern);
    }
}
