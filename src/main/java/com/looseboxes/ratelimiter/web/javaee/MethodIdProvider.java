package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.javaee.uri.MethodLevelPathPatterns;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;

public final class MethodIdProvider implements IdProvider<Method, PathPatterns<String>> {

    private final IdProvider<Class<?>, PathPatterns<String>> classIdProvider;

    public MethodIdProvider() {
        this(new ClassIdProvider());
    }

    public MethodIdProvider(IdProvider<Class<?>, PathPatterns<String>> classIdProvider) {
        this.classIdProvider = Objects.requireNonNull(classIdProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> classLevelPathPatterns = classIdProvider.getId(method.getDeclaringClass());

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
