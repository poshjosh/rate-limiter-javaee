package com.looseboxes.ratelimiter.web.javaee.uri;

import java.util.List;

class Util {
    static String[] combine(String [] pathPatterns, List<String> patternsToAdd) {
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
