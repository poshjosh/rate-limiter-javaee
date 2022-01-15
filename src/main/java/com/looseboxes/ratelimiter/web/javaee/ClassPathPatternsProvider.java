package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.IdProvider;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.javaee.uri.ClassLevelPathPatterns;

import javax.ws.rs.Path;

public class ClassPathPatternsProvider implements IdProvider<Class<?>, PathPatterns<String>> {

    public ClassPathPatternsProvider() { }

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
