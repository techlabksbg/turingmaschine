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
public class StateHeader {
    public Direction defaultDir = Direction.R;
    public StateName defaultNextState = null;
    public StateName name = null;
    int line;
    
    public StateHeader(String line, int l) {
        this.line = l;
        int p = line.indexOf(" ");
        if (p==-1) {
            name = new StateName(line, this.line);
            defaultNextState = name;
        } else {
            name = new StateName(line.substring(0, p), this.line);
            defaultNextState = name;
            while (p<line.length() && line.charAt(p)==' ') p++;
            if (p<line.length()) {
                defaultDir = Direction.fromString(line.substring(p,p+1));
                p++;
                while (p<line.length() && line.charAt(p)==' ') p++;
                if (p<line.length()) {
                    defaultNextState = new StateName(line.substring(p), this.line);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return name.toString()+" "+defaultDir+" "+defaultNextState;
    }
}
