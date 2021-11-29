package com.looseboxes.ratelimiter.web.javaee.uri;

import com.looseboxes.ratelimiter.web.core.util.PathPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

class PathPatternsImpl implements PathPatterns<String> {

    private static final Logger LOG = LoggerFactory.getLogger(PathPatternsImpl.class);

    private final List<String> patterns;
    private final PatternMatcher [] patternMatchers;

    PathPatternsImpl(PathPatternParser pathPatternParser, String... patterns) {
        this.patterns = Arrays.asList(patterns);
        this.patternMatchers = this.patterns.stream()
                .map(pathPatternParser).toArray(PatternMatcher[]::new);
        LOG.trace("Path patterns: {}", Arrays.toString(patterns));
    }

    @Override
    public PathPatterns<String> combine(PathPatterns<String> other) {
        // issue #001 For now Parent patterns must always return a child type from combine method
        return new PathPatternsImpl(
                PatternMatchers::child, // Always return a child from combination
                combine(getPatterns(), other.getPatterns()));
    }

    private String[] combine(List<String> existing, List<String> toAdd) {
        final int existingAmount = existing.size();
        final int amountToAdd = toAdd.size();
        final String [] all = new String[existingAmount * amountToAdd];
        int n = 0;
        for (String value : existing) {
            for (String s : toAdd) {
                all[n] = value + s;
                ++n;
            }
        }
        return all;
    }

    @Override
    public boolean matches(String uri) {
        if(LOG.isTraceEnabled()) {
            LOG.trace("Checking if: {} matches start of any: {}", uri, Arrays.toString(patternMatchers));
        }
        for(PatternMatcher pathPattern : patternMatchers) {
            if(pathPattern.matches(uri)) {
                LOG.trace("Matches start: true, uri: {}, pathPattern: {}", uri, pathPattern);
                return true;
            }
        }
        LOG.trace("Matches start: false, uri: {}", uri);
        return false;
    }

    @Override public List<String> getPatterns() {
        return patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathPatternsImpl that = (PathPatternsImpl) o;
        return Arrays.equals(patternMatchers, that.patternMatchers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(patternMatchers);
    }

    @Override
    public String toString() {
        return "PathPatternsImpl{" +
                "patternMatchers=" + Arrays.toString(patternMatchers) +
                '}';
    }
}
