package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.javaee.uri.MethodLevelPathPatterns;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;

public class MethodIdProvider implements AnnotatedElementIdProvider<Method, PathPatterns<String>> {

    private final AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProvider;

    public MethodIdProvider(AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> annotatedElementIdProvider) {
        this.annotatedElementIdProvider = Objects.requireNonNull(annotatedElementIdProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> classLevelPathPatterns = annotatedElementIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(Path.class) != null) {
            return buildPathPatterns(classLevelPathPatterns, method.getAnnotation(Path.class).value());
        }

        return classLevelPathPatterns;
    }

    private PathPatterns<String> buildPathPatterns(
            PathPatterns<String> pathPatterns, String subPathPattern) {
        if(subPathPattern == null || subPathPattern.isEmpty()) {
            return PathPatterns.none();
        }
        return pathPatterns.combine(new MethodLevelPathPatterns(subPathPattern));
    }
}
