package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.annotation.Element;
import com.looseboxes.ratelimiter.annotations.Rate;
import com.looseboxes.ratelimiter.util.Matcher;
import com.looseboxes.ratelimiter.web.core.MatcherFactory;
import com.looseboxes.ratelimiter.web.core.RequestToIdConverter;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import com.looseboxes.ratelimiter.web.core.util.PathPatternsMatcher;
import com.looseboxes.ratelimiter.web.core.util.PathPatternsProvider;
import com.looseboxes.ratelimiter.web.javaee.uri.JavaeePathPatternsProvider;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

public abstract class ResourceLimiterConfigJaveee
        extends ResourceLimiterConfig<ContainerRequestContext> {

    private static PathPatternsProvider pathPatternsProvider = new JavaeePathPatternsProvider();
    private static RequestToIdConverter<ContainerRequestContext, String> requestToUriConverter =
            new RequestToUriConverter();

    private static final class MatcherFactoryJavaee
            implements MatcherFactory<ContainerRequestContext, Element> {
        //@MatchRequestBy()
        //@Rate(5, headerName="", headerValue="")
        //@Rate(10, paramName="", paramValue="")
        //@Rate(1, sessionId="")
        //@Rate(
        @Rate
        @Override
        public Optional<Matcher<ContainerRequestContext, ?>> createMatcher(String name, Element e) {
            PathPatterns<String> pathPatterns = pathPatternsProvider.get(e);
            return Optional.of(new PathPatternsMatcher<>(pathPatterns, requestToUriConverter));
        }
    }

    public static Builder<ContainerRequestContext> builder() {
        return ResourceLimiterConfig.<ContainerRequestContext>builder()
            .matcherFactory(new MatcherFactoryJavaee());
            //.pathPatternsProvider(pathPatternsProvider)
            //.requestToIdConverter(requestToUriConverter);

    }
}
