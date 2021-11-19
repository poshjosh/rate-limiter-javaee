package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.PathPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BasicMethodPathPatterns implements PathPatterns<String> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicMethodPathPatterns.class);

    private final String [] pathPatterns;

    public BasicMethodPathPatterns(String... pathPatterns) {
        this.pathPatterns = Objects.requireNonNull(pathPatterns);
        LOG.trace("Path patterns: {}", Arrays.toString(pathPatterns));
    }

    public PathPatterns<String> combine(PathPatterns<String> other) {
        return new BasicMethodPathPatterns(Util.combine(pathPatterns, other));
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {} matches any: {}", uri, Arrays.toString(pathPatterns));
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

    @Override public List<String> getPathPatterns() {
        return Arrays.asList(pathPatterns);
    }

    @Override
    public String toString() {
        return "BasicMethodPathPatterns{" +
                "pathPatterns=" + Arrays.toString(pathPatterns) +
                '}';
    }
}
