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
public class MFunction {
    public MHeader header = null;
    MBody body = null;
    int firstLine = 0;
    int lastLine = 0;
    MFunction(String[] lines, int line, int endLine) {
        firstLine = line;
        try {
            header = new MHeader(lines[line], line);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error when trying to parse MHeader in line %d: %s\n",line, lines[line]),e);
        }
        line++;
        try {
            body = new MBody(lines, line, endLine);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Error when trying to parse MBody in line %d: %s\n",line, lines[line]),e);
        }  
        lastLine = body.lastLine+1;
    }
    
    @Override
    public String toString() {
        return header.toString()+"\n"+body.toString()+"@end\n\n";
    }
    
    public void translate(String src, String dest) {
        body.translate(src, dest);
    }

    void addTo(Alphabet a) {
        body.addTo(a);
    }

}
