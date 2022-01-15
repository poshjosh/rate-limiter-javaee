package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.util.DefaultClassesInPackageFinder;
import com.looseboxes.ratelimiter.web.core.DefaultResourceClassesSupplier;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.ws.rs.Path;

public class ResourceClassesSupplierImpl extends DefaultResourceClassesSupplier {
    public ResourceClassesSupplierImpl(RateLimitProperties rateLimitProperties) {
        super(new DefaultClassesInPackageFinder(), rateLimitProperties.getResourcePackages(), Path.class);
    }
}
