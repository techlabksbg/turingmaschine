/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import tm.machine.Alphabet;

/**
 *
 * @author ivo
 */
public class Command {
    public Symbol write = null;
    public Direction dir = Direction.N;
    Command(String c) {
        if (c.charAt(0)=='P') {
            if (c.length()<2) {
                throw new IllegalArgumentException("Missing character to print immediately after P command");
            }
            write = new Symbol(c.substring(1));
        } else {
            dir = Direction.fromString(c);
        }
    }
    
    @Override
    public String toString() {
        if (write==null) return dir.toString();
        return "P"+write;
    }

    void addTo(Alphabet a) {
        if (write!=null) {
            write.addTo(a);
        }
    }

    void translate(String src, String dest) {
        if (write!=null) {
            write.translate(src, dest);
        }
    }
    
}
