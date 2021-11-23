package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.AnnotatedElementIdProvider;
import com.looseboxes.ratelimiter.web.core.PathPatterns;
import com.looseboxes.ratelimiter.web.javaee.uri.ClassLevelPathPatterns;

import javax.ws.rs.Path;

public class ClassIdProvider implements AnnotatedElementIdProvider<Class<?>, PathPatterns<String>> {

    public ClassIdProvider() { }

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

        return new ClassLevelPathPatterns(path);
    }
}
