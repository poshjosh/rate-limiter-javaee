package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.RateLimiterFactory;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterConfigurer;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterContext;
import io.github.poshjosh.ratelimiter.web.core.RateLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
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

    private final RateLimiterRegistry rateLimiterRegistry;

    private final RateLimiterFactory<HttpServletRequest> rateLimiterFactory;

    private final ContainerRequestFilter containerRequestFilter;

    @Context
    private HttpServletRequest httpServletRequest;

    protected RateLimitingDynamicFeature(RateLimitProperties properties) {
        this(properties, registries -> {});
    }

    protected RateLimitingDynamicFeature(
            RateLimitProperties properties, RateLimiterConfigurer configurer) {

        RateLimiterContext config =
                rateLimiterContextBuilder(properties, configurer).build();

        this.rateLimiterRegistry = rateLimiterRegistry(config);

        if (config.getResourceClassesSupplier().get().isEmpty()) {
            this.rateLimiterFactory = RateLimiterFactory.noop();
        } else {
            this.rateLimiterFactory = this.rateLimiterRegistry.createRateLimiterFactory();
        }

        this.containerRequestFilter = reqContext -> {
            HttpServletRequest req = RateLimitingDynamicFeature.this.getHttpServletRequest();
            Objects.requireNonNull(req, "Injected HttpServletRequest is null");
            if (!tryConsume(req)) {
                RateLimitingDynamicFeature.this.onLimitExceeded(req, reqContext);
            }
        };

        LOG.info(rateLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    protected boolean tryConsume(HttpServletRequest httpRequest) {
        return rateLimiterFactory.getRateLimiter(httpRequest).tryAcquire();
    }

    protected RateLimiterRegistry rateLimiterRegistry(RateLimiterContext config) {
        return RateLimiterRegistryJavaee.of(config);
    }

    protected RateLimiterContext.Builder rateLimiterContextBuilder(
            RateLimitProperties properties, RateLimiterConfigurer configurer) {
        return RateLimiterContextJavaee.builder()
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
        return rateLimiterRegistry.isRateLimitingEnabled() && isRateLimited(resourceInfo);
    }

    public int getPriority() {
        return 0; // As early as possible
    }

    public boolean isRateLimited(ResourceInfo resourceInfo) {
        final Method method = resourceInfo.getResourceMethod();
        if (method == null) {
            final Class<?> clazz = resourceInfo.getResourceClass();
            return clazz != null && rateLimiterRegistry.isRateLimited(clazz);
        }
        return rateLimiterRegistry.isRateLimited(method);
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public RateLimiterRegistry getRateLimiterRegistry() {
        return rateLimiterRegistry;
    }

    public RateLimiterFactory<HttpServletRequest> getRateLimiterFactory() { return rateLimiterFactory; }
}
