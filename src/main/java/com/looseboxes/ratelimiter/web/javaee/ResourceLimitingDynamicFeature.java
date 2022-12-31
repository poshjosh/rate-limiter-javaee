package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.Registries;
import com.looseboxes.ratelimiter.web.core.ResourceLimiterConfigurer;
import com.looseboxes.ratelimiter.web.core.WebResourceLimiterConfig;
import com.looseboxes.ratelimiter.web.core.impl.WebResourceLimiter;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.*;

public abstract class ResourceLimitingDynamicFeature implements DynamicFeature,
        ResourceLimiterConfigurer<ContainerRequestContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLimitingDynamicFeature.class);

    private final ContainerRequestFilter containerRequestFilter;

    private final List<Class<?>> resourceClasses;

    private final RateLimitProperties properties;

    private final WebResourceLimiterConfig<ContainerRequestContext> webResourceLimiterConfig;

    private final WebResourceLimiter<ContainerRequestContext> resourceLimiter;

    protected ResourceLimitingDynamicFeature(RateLimitProperties properties) {
        this(WebResourceLimiterConfigJavaee.builder().properties(properties));
    }

    private ResourceLimitingDynamicFeature(
            WebResourceLimiterConfig.Builder<ContainerRequestContext> webResourceLimiterConfigBuilder) {

        this.webResourceLimiterConfig = webResourceLimiterConfigBuilder.configurer(this).build();

        this.properties = webResourceLimiterConfig.getProperties();

        this.resourceClasses = webResourceLimiterConfig.getResourceClassesSupplier().get();

        this.resourceLimiter = new WebResourceLimiter<>(webResourceLimiterConfig);

        this.containerRequestFilter = requestContext -> {
            if (!resourceLimiter.tryConsume(requestContext)) {
                ResourceLimitingDynamicFeature.this.onLimitExceeded(requestContext);
            }
        };

        LOG.info("Completed automatic setup of rate limiting");
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
    public void configure(Registries<ContainerRequestContext> registries) { }

    /**
     * Called when a limit is exceeded.
     *
     * @param requestContext the request context
     */
    protected void onLimitExceeded(ContainerRequestContext requestContext) { }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        if(isEnabled(properties) && isTargetedResource(resourceInfo.getResourceClass())) {
            // final int priority = Integer.MIN_VALUE; // Set rate limiting to highest possible priority
            // final int priority = Priorities.AUTHENTICATION - 1; // Set rate limiting just before authentication
            final int priority = -1; // We can easily see a situation where there are multiple zero priority components
            featureContext.register(containerRequestFilter, priority);
        }
    }

    private boolean isEnabled(RateLimitProperties properties) {
        final Boolean disabled = properties.getDisabled();
        return disabled == null || Boolean.FALSE.equals(disabled);
    }

    public boolean isTargetedResource(Class<?> clazz) {
        return resourceClasses.contains(clazz);
    }

    public WebResourceLimiter<ContainerRequestContext> getResourceLimiter() {
        return resourceLimiter;
    }

    public WebResourceLimiterConfig<ContainerRequestContext> getWebRequestRateLimiterConfig() {
        return webResourceLimiterConfig;
    }
}
