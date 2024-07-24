package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.model.RateSource;
import io.github.poshjosh.ratelimiter.util.StringUtils;
import io.github.poshjosh.ratelimiter.web.core.util.*;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.*;

public class ResourceInfoProviderJavaee implements ResourceInfoProvider {

    private static final Class<? extends Annotation> [] httpMethodClasses =
            new Class[]{GET.class, POST.class, PUT.class, DELETE.class,
                    PATCH.class, HEAD.class, OPTIONS.class};

    @Override
    public ResourceInfo get(RateSource source) {
        return ResourceInfos.of(getResourcePath(source), getMethods(source));
    }

    private ResourcePath<String> getResourcePath(RateSource source) {
        if (source.isOwnDeclarer()) {
            return getClassResourcePath(source).orElse(ResourcePaths.matchNone());
        }
        return getMethodResourcePath(source);
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

    private Optional<ResourcePath<String>> getClassResourcePath(RateSource source) {

        final Path pathAnnotation = source.getAnnotation(Path.class).orElse(null);

        if(pathAnnotation == null) {
            return Optional.empty();
        }

        final String path = pathAnnotation.value();

        if(!StringUtils.hasText(path)) {

            return Optional.of(ResourcePaths.matchNone());
        }

        return Optional.of(new ClassLevelResourcePath(path));
    }

    private ResourcePath<String> getMethodResourcePath(RateSource method) {

        final ResourcePath<String> classLevelResourcePath = method.getDeclarer()
                .flatMap(this::getClassResourcePath).orElse(ResourcePaths.matchNone());

        return method.getAnnotation(Path.class)
                .map(annotation -> composeResourcePath(classLevelResourcePath, annotation.value()))
                .orElse(classLevelResourcePath);
    }

    private ResourcePath<String> composeResourcePath(ResourcePath<String> resourcePath, String subPathPattern) {
        if(!StringUtils.hasText(subPathPattern)) {
            return resourcePath;
        }
        return resourcePath.combine(new MethodLevelResourcePath(subPathPattern));
    }
}
