package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
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

public abstract class ResourceLimitingDynamicFeature implements DynamicFeature {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitingDynamicFeature.class);

    private final ResourceLimiterRegistry resourceLimiterRegistry;

    private final ResourceLimiter<HttpServletRequest> resourceLimiter;

    private final ContainerRequestFilter containerRequestFilter;

    @Context
    private HttpServletRequest httpServletRequest;

    protected ResourceLimitingDynamicFeature(RateLimitProperties properties) {
        this(properties, registries -> {});
    }

    protected ResourceLimitingDynamicFeature(
            RateLimitProperties properties, ResourceLimiterConfigurer configurer) {

        ResourceLimiterConfig resourceLimiterConfig =
                resourceLimiterConfigBuilder(properties, configurer).build();

        this.resourceLimiterRegistry = resourceLimiterRegistry(resourceLimiterConfig);

        this.resourceLimiter = this.resourceLimiterRegistry.createResourceLimiter();

        this.containerRequestFilter = reqContext -> {
            HttpServletRequest req = ResourceLimitingDynamicFeature.this.getHttpServletRequest();
            Objects.requireNonNull(req, "Injected HttpServletRequest is null");
            if (!tryConsume(req)) {
                ResourceLimitingDynamicFeature.this.onLimitExceeded(req, reqContext);
            }
        };

        LOG.info(resourceLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    protected boolean tryConsume(HttpServletRequest httpRequest) {
        return getResourceLimiter().tryConsume(httpRequest);
    }

    protected ResourceLimiterRegistry resourceLimiterRegistry(ResourceLimiterConfig config) {
        return ResourceLimiterRegistryJavaee.of(config);
    }

    protected ResourceLimiterConfig.Builder resourceLimiterConfigBuilder(
            RateLimitProperties properties, ResourceLimiterConfigurer configurer) {
        return ResourceLimiterConfigJavaee.builder()
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
        return resourceLimiterRegistry.isRateLimitingEnabled() && isRateLimited(resourceInfo);
    }

    public int getPriority() {
        return 0; // As early as possible
    }

    public boolean isRateLimited(ResourceInfo resourceInfo) {
        final Method method = resourceInfo.getResourceMethod();
        if (method == null) {
            final Class<?> clazz = resourceInfo.getResourceClass();
            return clazz != null && resourceLimiterRegistry.isRateLimited(clazz);
        }
        return resourceLimiterRegistry.isRateLimited(method);
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public ResourceLimiterRegistry getResourceLimiterRegistry() {
        return resourceLimiterRegistry;
    }

    public ResourceLimiter<HttpServletRequest> getResourceLimiter() { return resourceLimiter; }
}
