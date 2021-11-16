package com.looseboxes.ratelimiter.javaee.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedRequestMappingImpl implements AnnotatedRequestMapping {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedRequestMappingImpl.class);

    public AnnotatedRequestMappingImpl(String... pathPatterns) {
        throw new UnsupportedOperationException();
    }

    public AnnotatedRequestMapping combine(String... uris) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean matches(String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean matchesStartOf(String uri) {
        throw new UnsupportedOperationException();
    }
}
