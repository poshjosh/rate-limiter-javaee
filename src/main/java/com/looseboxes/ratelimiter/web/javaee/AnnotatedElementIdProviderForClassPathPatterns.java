package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.RequestPathPatterns;
import com.looseboxes.ratelimiter.web.core.RequestPathPatternsImpl;

import javax.ws.rs.Path;
import java.util.Objects;

public class AnnotatedElementIdProviderForClassPathPatterns implements AnnotatedElementIdProvider<Class<?>, RequestPathPatterns<String>> {

    private final RequestPathPatterns<String> applicationPathPatterns;

    public AnnotatedElementIdProviderForClassPathPatterns(
            RequestPathPatterns<String> applicationPathPatterns) {
        this.applicationPathPatterns = Objects.requireNonNull(applicationPathPatterns);
    }

    @Override
    public RequestPathPatterns<String> getId(Class<?> source) {

        final Path pathAnnotation = source.getAnnotation(Path.class);

        if(pathAnnotation == null) {
            return RequestPathPatterns.none();
        }

        final String path = pathAnnotation.value();

        if(path.isEmpty()) {

            return RequestPathPatterns.none();
        }

        return applicationPathPatterns.combine(new RequestPathPatternsImpl(path));
    }
}
