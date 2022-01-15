package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.javaee.uri.MethodLevelPathPatterns;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.Objects;

public class MethodPathPatternsProvider implements IdProvider<Method, PathPatterns<String>> {

    private final IdProvider<Class<?>, PathPatterns<String>> classPathPatternsProvider;

    public MethodPathPatternsProvider() {
        this(new ClassPathPatternsProvider());
    }

    public MethodPathPatternsProvider(IdProvider<Class<?>, PathPatterns<String>> classPathPatternsProvider) {
        this.classPathPatternsProvider = Objects.requireNonNull(classPathPatternsProvider);
    }

    @Override
    public PathPatterns<String> getId(Method method) {

        final PathPatterns<String> classLevelPathPatterns = classPathPatternsProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(Path.class) != null) {
            return composePathPatterns(classLevelPathPatterns, method.getAnnotation(Path.class).value());
        }

        return classLevelPathPatterns;
    }

    private PathPatterns<String> composePathPatterns(PathPatterns<String> pathPatterns, String subPathPattern) {
        if(subPathPattern == null || subPathPattern.isEmpty()) {
            return PathPatterns.none();
        }
        return pathPatterns.combine(new MethodLevelPathPatterns(subPathPattern));
    }
}
