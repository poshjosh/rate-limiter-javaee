package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;

import javax.ws.rs.Path;
import java.util.Objects;

public class AnnotatedElementIdProviderForClass implements AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> {

    private final PathPatterns<String> applicationPathPatterns;

    public AnnotatedElementIdProviderForClass(
            PathPatterns<String> applicationPathPatterns) {
        this.applicationPathPatterns = Objects.requireNonNull(applicationPathPatterns);
    }

    @Override
    public PathPatterns<String> getId(Class<?> source) {

        final Path pathAnnotation = source.getAnnotation(Path.class);

        if(pathAnnotation == null) {
            return PathPatterns.none();
        }

        final String path = pathAnnotation.value();

        if(path.isEmpty()) {

            return PathPatterns.none();
        }

        return applicationPathPatterns.combine(new BasicClassPathPatterns(path));
    }
}
