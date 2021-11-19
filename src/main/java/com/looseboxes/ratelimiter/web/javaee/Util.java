package com.looseboxes.ratelimiter.web.javaee;

import com.looseboxes.ratelimiter.web.core.PathPatterns;

import java.util.List;

class Util {
    static String[] combine(String [] pathPatterns, PathPatterns<String> other) {
        final List<String> patternsToAdd = other.getPathPatterns();
        final int size = patternsToAdd.size();
        final String [] all = new String[pathPatterns.length * size];
        int k = 0;
        for(int i = 0; i < pathPatterns.length; i++) {
            for(int j = 0; j < size; j++) {
                all[k] = pathPatterns[i] + patternsToAdd.get(j);
                ++k;
            }
        }
        return all;
    }
}
