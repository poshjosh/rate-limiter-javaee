package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.annotation.RateSource;
import io.github.poshjosh.ratelimiter.util.StringUtils;
import io.github.poshjosh.ratelimiter.web.core.util.PathPatterns;
import io.github.poshjosh.ratelimiter.web.core.util.ResourceInfoProvider;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.*;

public class ResourceInfoProviderJavaee implements ResourceInfoProvider {

    private static final Class<? extends Annotation> [] httpMethodClasses =
            new Class[]{GET.class, POST.class, PUT.class, DELETE.class,
                    PATCH.class, HEAD.class, OPTIONS.class};

    @Override
    public ResourceInfoProvider.ResourceInfo get(RateSource source) {
        return ResourceInfo.of(getPathPatterns(source), getMethods(source));
    }

    private PathPatterns<String> getPathPatterns(RateSource source) {
        if (source.isOwnDeclarer()) {
            return getClassPatterns(source).orElse(PathPatterns.none());
        }
        return getMethodPatterns(source);
    }

    private String [] getMethods(RateSource rateSource) {
        List<String> httpMethods = new ArrayList<>();
        for(Class<? extends Annotation> httpMethodClass : httpMethodClasses) {
            rateSource.getAnnotation(httpMethodClass)
                    .ifPresent(httpMethod -> {
                        // This returned class com.sun.proxy.$Proxy6
                        //httpMethods.add(httpMethod.getClass().getName());
                        httpMethods.add(httpMethodClass.getSimpleName());
                    });
        }
        return httpMethods.toArray(new String[0]);
    }

    private Optional<PathPatterns<String>> getClassPatterns(RateSource source) {

        final Path pathAnnotation = source.getAnnotation(Path.class).orElse(null);

        if(pathAnnotation == null) {
            return Optional.empty();
        }

        final String path = pathAnnotation.value();

        if(!StringUtils.hasText(path)) {

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
        if(!StringUtils.hasText(subPathPattern)) {
            return pathPatterns;
        }
        return pathPatterns.combine(new MethodLevelPathPatterns(subPathPattern));
    }
}
