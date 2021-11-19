package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.RequestPathPatterns;
import com.looseboxes.ratelimiter.web.core.RequestPathPatternsImpl;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotatedElementIdProviderForMethodPathPatterns implements AnnotatedElementIdProvider<Method, RequestPathPatterns<String>> {

    private final AnnotatedElementIdProvider<Class<?>, RequestPathPatterns<String>> annotatedElementIdProvider;

    public AnnotatedElementIdProviderForMethodPathPatterns(AnnotatedElementIdProvider<Class<?>, RequestPathPatterns<String>> annotatedElementIdProvider) {
        this.annotatedElementIdProvider = Objects.requireNonNull(annotatedElementIdProvider);
    }

    @Override
    public RequestPathPatterns<String> getId(Method method) {

        final RequestPathPatterns<String> classLevelPathPatterns = annotatedElementIdProvider.getId(method.getDeclaringClass());

        if (method.getAnnotation(Path.class) != null) {
            return buildPathPatterns(classLevelPathPatterns, method.getAnnotation(Path.class).value());
        }

        return classLevelPathPatterns;
    }

    private RequestPathPatterns<String> buildPathPatterns(
            RequestPathPatterns<String> requestPathPatterns, String subPathPattern) {
        if(subPathPattern == null || subPathPattern.isEmpty()) {
            return RequestPathPatterns.none();
        }
        return requestPathPatterns.combine(new RequestPathPatternsImpl(subPathPattern));
    }
}
