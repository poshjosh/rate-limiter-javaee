package io.github.poshjosh.ratelimiter.web.javaee;

import io.github.poshjosh.ratelimiter.ResourceLimiter;
import io.github.poshjosh.ratelimiter.web.core.Registries;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfig;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterRegistry;
import io.github.poshjosh.ratelimiter.web.core.util.RateLimitProperties;
import io.github.poshjosh.ratelimiter.web.core.ResourceLimiterConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;

public abstract class ResourceLimitingDynamicFeature implements DynamicFeature,
        ResourceLimiterConfigurer<ContainerRequestContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitingDynamicFeature.class);

    private final ResourceLimiterRegistry<ContainerRequestContext> resourceLimiterRegistry;

    private final ResourceLimiter<ContainerRequestContext> resourceLimiter;

    private final ContainerRequestFilter containerRequestFilter;

    protected ResourceLimitingDynamicFeature(RateLimitProperties properties) {
        this(ResourceLimiterConfigJaveee.builder().properties(properties));
    }

    private ResourceLimitingDynamicFeature(
            ResourceLimiterConfig.Builder<ContainerRequestContext> webResourceLimiterConfigBuilder) {

        ResourceLimiterConfig<ContainerRequestContext> resourceLimiterConfig =
                webResourceLimiterConfigBuilder.configurer(this).build();

        this.resourceLimiterRegistry = ResourceLimiterRegistryJavaee.of(resourceLimiterConfig);

        this.resourceLimiter = this.resourceLimiterRegistry.createResourceLimiter();

        this.containerRequestFilter = requestContext -> {
            if (!resourceLimiter.tryConsume(requestContext)) {
                ResourceLimitingDynamicFeature.this.onLimitExceeded(requestContext);
            }
        };

        LOG.info(resourceLimiterRegistry.isRateLimitingEnabled()
                ? "Completed setup of automatic rate limiting" : "Rate limiting is disabled");
    }

    /**
     * Overried this method for find-grained configuration of rate limiting.
     * <p>Below is some basic examples of configuraing rate limiting</p>
     * <pre>
     * <code>
     * @Component
     * public class MyResourceLimiterConfigurer implements ResourceLimiterConfigurer<HttpServletRequest>{
     *   public void configure(Registries<HttpServletRequest> registries) {
     *     // Register consumption listeners
     *     // ------------------------------
     *
     *     registries.listeners().register((context, resourceId, hits, limit) -> {
     *
     *       // For example, log the limit that was exceeded
     *       System.out.println("For " + resourceId + ", the following limits are exceeded: " + limit);
     *     });
     *
     *     // Register request matchers
     *     // -------------------------
     *
     *     // Identify resources to rate-limit by session id
     *     registries.matchers().register("limitBySession", request -> request.getSession().getId());
     *
     *     // Identify resources to rate-limit by the presence of request parameter "utm_source"
     *     registries.matchers().register("limitByUtmSource", request -> request.getParameter("utm_source"));
     *
     *     // Rate limit users from a specific utm_source e.g facebook
     *     registries.matchers().register("limitByUtmSourceIsFacebook",
     *             request -> "facebook".equals(request.getParameter("utm_source")));
     *
     *     // You could use a variety of Cache flavours
     *     // -----------------------------------------
     *
     *     javax.cache.Cache javaxCache = null; // PROVIDE THIS
     *     registries.caches().register("limitBySession", new JavaRateCache<>(javaxCache));
     *   }
     * }
     * </code>
     * </pre>
     *
     * @param registries provide various registries which expose register methods for configuring
     *                   rate limiting
     */
    @Override
    public abstract void configure(Registries<ContainerRequestContext> registries);

    /**
     * Called when a limit is exceeded.
     *
     * @param requestContext the request context
     */
    protected void onLimitExceeded(ContainerRequestContext requestContext) { }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(resourceLimiterRegistry.isRateLimitingEnabled() && isRateLimited(resourceInfo)) {
            // final int priority = Integer.MIN_VALUE; // Set rate limiting to highest possible priority
            // final int priority = Priorities.AUTHENTICATION - 1; // Set rate limiting just before authentication
            final int priority = -1; // We can easily see a situation where there are multiple zero priority components
            LOG.debug("Registering request rate limiting filter for: {}", resourceInfo);
            featureContext.register(containerRequestFilter, priority);
        }
    }

    public boolean isRateLimited(ResourceInfo resourceInfo) {
        final Method method = resourceInfo.getResourceMethod();
        if (method == null) {
            final Class<?> clazz = resourceInfo.getResourceClass();
            return clazz != null && resourceLimiterRegistry.isRateLimited(clazz);
        }
        return resourceLimiterRegistry.isRateLimited(method);
    }

    public ResourceLimiterRegistry<ContainerRequestContext> getResourceLimiterRegistry() {
        return resourceLimiterRegistry;
    }

    public ResourceLimiter<ContainerRequestContext> getResourceLimiter() {
        return resourceLimiter;
    }
}
