package com.looseboxes.ratelimiter.javaee.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class AnnotatedRequestMappingImpl implements AnnotatedRequestMapping {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedRequestMappingImpl.class);

    private final String [] pathPatterns;

    public AnnotatedRequestMappingImpl(String... pathPatterns) {
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
        LOG.trace("Path patterns: {}", Arrays.toString(pathPatterns));
    }

    public AnnotatedRequestMapping combine(String... uris) {
        final String [] all = new String[pathPatterns.length * uris.length];
        int k = 0;
        for(int i = 0; i < pathPatterns.length; i++) {
            for(int j = 0; j < uris.length; j++) {
                all[k] = pathPatterns[i] + uris[j];
                ++k;
            }
        }
        return new AnnotatedRequestMappingImpl(all);
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {} matches: {}", uri, Arrays.toString(pathPatterns));
        }
        for(String pathPattern : pathPatterns) {
            if(pathPattern.equals(uri)) {
                LOG.trace("Matches: true, uri: {}, pathPattern: {}", uri, pathPattern);
                return true;
            }
        }
        LOG.trace("Matches: false, uri: {}", uri);
        return false;
    }

    @Override
    public boolean matchesStartOf(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {}, matches start of any: {}", uri, Arrays.toString(pathPatterns));
        }
        for(String pathPattern : pathPatterns) {
            if(pathPattern.startsWith(uri)) {
                LOG.trace("Matches start: true, uri: {}, pathPattern: {}", uri, pathPattern);
                return true;
            }
        }
        LOG.trace("Matches start: false, uri: {}", uri);
        return false;
    }

    @Override
    public String toString() {
        return "AnnotatedRequestMappingImpl{" +
                "pathPatterns=" + Arrays.toString(pathPatterns) +
                '}';
    }
}
