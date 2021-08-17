/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import java.util.ArrayList;
import tm.machine.Alphabet;

/**
 *
 * @author ivo
 */
public class MBody {
    ArrayList<State> states = new ArrayList<>();
    int lastLine = 0;
    MBody(String[] lines, int line, int endLine) {
        int el = line;
        while (el<endLine && !lines[el].startsWith("@end")) el++;
        if (el==endLine) {
            throw new IllegalArgumentException(String.format("missing @end in MFunction-Body starting at line %d",line));
        }
        lastLine = el;
        while (true) {
            while (line<el && (lines[line].isEmpty() || lines[line].startsWith("#"))) line++;
            if (line==el) {
                break;
            }
            try {
                State s = new State(lines, line, el);
                line = s.lastLine+1;
                states.add(s);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Error when trying to parse State in line %d: %s\n",line, lines[line]),e);
            }
            if (line>=el) {
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (State s : states) {
            for (String l : s.toString().split("\n")) {
                sb.append("   ").append(l).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public void translate(String src, String dest) {
        for (State s : states) {
            s.translate(src, dest);
        }
    }

    void addTo(Alphabet a) {
        for (State s : states) {
            s.addTo(a);
        }
    }

    
}
