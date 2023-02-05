package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.annotation.RateSource;
import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import io.github.poshjosh.ratelimiter.web.core.util.PathPatternsProvider;

import javax.ws.rs.Path;
import java.util.Optional;

public class PathPatternsProviderJavaee implements PathPatternsProvider {

    @Override
    public PathPatterns<String> get(RateSource source) {
        if (source.isOwnDeclarer()) {
            return getClassPatterns(source).orElse(PathPatterns.none());
        }
        return getMethodPatterns(source);
    }

    private Optional<PathPatterns<String>> getClassPatterns(RateSource source) {

        final Path pathAnnotation = source.getAnnotation(Path.class).orElse(null);

        if(pathAnnotation == null) {
            return Optional.empty();
        }

        final String path = pathAnnotation.value();

        if(path.isEmpty()) {

            return Optional.of(PathPatterns.none());
        }

        return Optional.of(new ClassLevelPathPatterns(path));
    }

    private PathPatterns<String> getMethodPatterns(RateSource method) {

        final PathPatterns<String> classLevelPathPatterns = method.getDeclarer()
                .flatMap(this::getClassPatterns).orElse(PathPatterns.none());

        return method.getAnnotation(Path.class)
                .map(annotation -> composePathPatterns(classLevelPathPatterns, annotation.value()))
                .orElse(classLevelPathPatterns);
    }

    private PathPatterns<String> composePathPatterns(PathPatterns<String> pathPatterns, String subPathPattern) {
        if(subPathPattern == null || subPathPattern.isEmpty()) {
            return pathPatterns;
        }
        return pathPatterns.combine(new MethodLevelPathPatterns(subPathPattern));
    }
}
