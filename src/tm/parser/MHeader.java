/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ivo
 */
public class MHeader {
    
    private static final Pattern HEADER_REGEXP = Pattern.compile("^@([a-zA-Z0-9_]+)\\(\\s*(\\d+)\\s*;\\s*(\\d+)\\)$");
    
    String name;
    int numSymbols;
    int numStates;
    int line;
    MHeader(String line, int l) {
        this.line = l;
        Matcher m = HEADER_REGEXP.matcher(line);
        if (m.find()) {
            MatchResult mr = m.toMatchResult();
            name = mr.group(1);
            numSymbols = Integer.parseInt(mr.group(2));
            numStates = Integer.parseInt(mr.group(3));
        } else {
            throw new IllegalArgumentException(String.format("This is not a valid MFunction Header: %s\n", line));
        }
    }
    
    @Override
    public String toString() {
        return "@"+name+"("+numSymbols+";"+numStates+")";
    }
}
