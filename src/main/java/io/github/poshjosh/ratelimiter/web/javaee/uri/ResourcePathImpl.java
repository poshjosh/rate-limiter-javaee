package io.github.poshjosh.ratelimiter.web.javaee.uri;

import io.github.poshjosh.ratelimiter.web.core.util.ResourcePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

class ResourcePathImpl implements ResourcePath {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcePathImpl.class);

    private final List<String> patterns;
    private final PatternMatcher [] patternMatchers;

    ResourcePathImpl(PathPatternParser pathPatternParser, String... patterns) {
        this.patterns = Arrays.asList(patterns);
        this.patternMatchers = this.patterns.stream()
                .map(pathPatternParser).toArray(PatternMatcher[]::new);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Path patterns: {}", Arrays.toString(patterns));
        }
    }

    @Override
    public ResourcePath combine(ResourcePath other) {
        // issue #001 For now Parent patterns must always return a child type from combine method
        return new ResourcePathImpl(
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
                return true;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Matches start: false, uri: {}", uri);
        }
        return false;
    }

    @Override public List<String> getPatterns() {
        return patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePathImpl that = (ResourcePathImpl) o;
        return Arrays.equals(patternMatchers, that.patternMatchers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(patternMatchers);
    }

    @Override
    public String toString() {
        return "ResourcePathImpl{" + Arrays.toString(patternMatchers) + '}';
    }
}
