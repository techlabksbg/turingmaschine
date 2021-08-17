/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import java.util.ArrayList;
import java.util.HashMap;
import tm.TM;
import tm.machine.Alphabet;
import tm.machine.Machine;
import tm.machine.Tape;

/**
 *
 * @author ivo
 */
public class Parser {
    HashMap<String, MFunction> mFunctions = new HashMap<>();
    ArrayList<State> states = new ArrayList<>();
//    Alphabet alphabet;
    Tape tape;
    
    public Parser(String text, Tape tape, boolean keepTape) {
//        alphabet = new Alphabet(tape);
        this.tape = tape;
//        Symbol.alphabet = alphabet;
        String[] lines = getLines(text);
        int line = 0;
        int endLine = lines.length;
        while (line<endLine) {
            while (line<endLine && (lines[line].isEmpty() || lines[line].startsWith("#"))) {
                if (lines[line].startsWith("#tape") && !keepTape) {
                    String t = lines[line].substring(6).replaceAll("\\s", "");
                    //alphabet.add(t);
                    tape.setContents(t);
                }
                line++;
            }
            if (line==endLine) break;
            //if (TM.verbose) System.out.format("Parsing line %d: %s\n", line, lines[line]);
            if (lines[line].startsWith("alphabet")) {
                //alphabet.add(lines[line].substring(8).replaceAll("\\s", ""));
                line++;
            } else if (lines[line].startsWith("@")) {
                try {
                    MFunction mf = new MFunction(lines, line, endLine);
                    line = mf.lastLine;
                    mFunctions.put(mf.header.name, mf);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Error when trying to parse MFunction in line %d: %s",line, lines[line]),e);
                }
            } else {
                try {
                    State s = new State(lines, line, endLine);
                    line = s.lastLine;
                    states.add(s);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Error when trying to parse State in line %d: %s",line, lines[line]),e);
                }
            }
        }
    }
    
    public void declareAndImplement(ArrayList<State> states, Scope scope, Machine machine) {
        for (State s : states) {
            s.declare(this, machine, scope);
        }
        for (State s : states) {
            s.implement(this, machine, scope);
        }
    }
    
    private void addTo(Alphabet a) {
        for (State s : states) {
            s.addTo(a);
        }
        for (MFunction mf : mFunctions.values()) {
            mf.addTo(a);
        }
    }
    
    public Machine instanciate() {
        Alphabet a = new Alphabet();
        a.add(tape.toString());
        addTo(a);
        Machine m = new Machine(a,tape);
        declareAndImplement(states, Scope.root, m);
        if (!m.states.exists("start")) {
            m.states.link("start", states.get(0).header.name.getName(Scope.root,m));
        }
        return m;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (State s : states) {
            sb.append(s.toString());
        }
        for (MFunction mf : mFunctions.values()) {
            sb.append(mf.toString());
        }
        return sb.toString();
    }
    private String[] getLines(String text) {
        String[] lines = text.split("\n");
        for (int i=0; i<lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        return lines;
    }
    
    public void translate(String src, String dest) {
        if (src.length()!=dest.length()) {
            throw new IllegalArgumentException("Translating Alphabete: Number of symbols in source and destination is not equal!");
        }
        for (State s : states) {
            s.translate(src, dest);
        }
        for (MFunction mf : mFunctions.values()) {
            mf.translate(src, dest);
        }
        tape.translate(src, dest);
        //System.out.println(tape);
    }
    
}
