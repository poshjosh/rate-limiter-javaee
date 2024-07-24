package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.RateLimiter;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistries;
import io.github.poshjosh.ratelimiter.web.core.WebRateLimiterRegistry;
import io.github.poshjosh.ratelimiter.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class RateLimitingDynamicFeature implements DynamicFeature {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitingDynamicFeature.class);

    private final boolean rateLimitingEnabled;
    private final WebRateLimiterRegistry webRateLimiterRegistry;

    private final ContainerRequestFilter containerRequestFilter;

    @Context
    private HttpServletRequest httpServletRequest;

    protected RateLimitingDynamicFeature(RateLimitProperties properties) {
        this(properties, registries -> {});
    }

    protected RateLimitingDynamicFeature(
            RateLimitProperties properties, RateLimiterConfigurer configurer) {

        this.rateLimitingEnabled = properties.isRateLimitingEnabled();

        WebRateLimiterContext context =
                rateLimiterContextBuilder(properties, configurer).build();

        this.webRateLimiterRegistry = rateLimiterRegistry(context);

        this.containerRequestFilter = reqContext -> {
            HttpServletRequest req = RateLimitingDynamicFeature.this.getHttpServletRequest();
            Objects.requireNonNull(req, "Injected HttpServletRequest is null");
            if (!tryConsume(req)) {
                RateLimitingDynamicFeature.this.onLimitExceeded(req, reqContext);
            }
        };

        LOG.info(rateLimitingEnabled
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    protected boolean tryConsume(HttpServletRequest httpRequest) {
        return getRateLimiter(httpRequest).tryAcquire();
    }

    protected WebRateLimiterRegistry rateLimiterRegistry(WebRateLimiterContext context) {
        return WebRateLimiterRegistries.of(context);
    }

    protected WebRateLimiterContext.Builder rateLimiterContextBuilder(
            RateLimitProperties properties, RateLimiterConfigurer configurer) {
        return WebRateLimiterContextJavaee.builder()
                .properties(properties).configurer(configurer);
    }

    /**
     * Called when a limit is exceeded.
     *
     * @param request the http servlet request
     * @param requestContext the request context
     */
    protected void onLimitExceeded(
            HttpServletRequest request, ContainerRequestContext requestContext) { }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(isRateLimitingEnabledFor(resourceInfo)) {
            LOG.debug("Registering request rate limiting filter for: {}", resourceInfo);
            featureContext.register(containerRequestFilter, getPriority());
        }
    }

    public boolean isRateLimitingEnabledFor(ResourceInfo resourceInfo) {
        return rateLimitingEnabled && isRateLimited(resourceInfo);
    }

    public int getPriority() {
        return 0; // As early as possible
    }

    public boolean isRateLimited(ResourceInfo resourceInfo) {
        final Method method = resourceInfo.getResourceMethod();
        if (method == null) {
            final Class<?> clazz = resourceInfo.getResourceClass();
            return clazz != null && webRateLimiterRegistry.isRegistered(clazz);
        }
        return webRateLimiterRegistry.isRegistered(method);
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public RateLimiter getRateLimiter(HttpServletRequest httpRequest) {
        return webRateLimiterRegistry.getRateLimiterOrUnlimited(httpRequest);
    }

    public WebRateLimiterRegistry getRateLimiterRegistry() {
        return webRateLimiterRegistry;
    }
}
