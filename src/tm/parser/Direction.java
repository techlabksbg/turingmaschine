/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

/**
 *
 * @author ivo
 */
public enum Direction {
    L(-1), R(1), N(0);
    
    private final int d;
    
    Direction(int d) {
        this.d = d;
    }

    public int getOffset() {
        return d;
    }
    
    public static Direction fromString(String l) {
        switch (l.toUpperCase()) {
        case "R":
            return R;
        case "L":
            return L;
        case "N":
            return N;
        }
        throw new IllegalArgumentException(String.format("'%s' is not a valid direction.", l));
    }
    
}
