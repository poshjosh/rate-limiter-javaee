package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.util.ClassesInPackageFinderImpl;
import com.looseboxes.ratelimiter.web.core.DefaultResourceClassesSupplier;
import com.looseboxes.ratelimiter.web.core.util.RateLimitProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;

@Singleton
public class ResourceClassesSupplierImpl extends DefaultResourceClassesSupplier {

    @Inject
    public ResourceClassesSupplierImpl(RateLimitProperties rateLimitProperties) {
        super(new ClassesInPackageFinderImpl(), rateLimitProperties.getResourcePackages(), Path.class);
    }
}
