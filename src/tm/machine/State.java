/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

import java.util.Arrays;
import tm.parser.Direction;
import tm.parser.Scope;
import tm.parser.StateHeader;

/**
 *
 * @author ivo
 */
public class State {
    String name;
    char[] write;
    int[] dir;
    State[] next;
    int id;
    
    Machine machine;
    
    State(Machine m, String name) {
        machine = m;
        this.name = name;
        int n = m.alphabet.size();
        // Directions
        dir = new int[n];
        Arrays.fill(dir, 0);
        // States
        next = new State[n];
        Arrays.fill(next, this);
        // writes
        write = m.alphabet.getCharArray();
    }

    State(Machine m, String name, Direction defaultDir) {
        this(m,name);
        Arrays.fill(dir, defaultDir.getOffset());
    }
    
    public void setDefaultState(String name) {
        Arrays.fill(next, machine.states.get(name));
    }
    
    State(Machine m) {
        name = "stop";
        machine = m;
        int n = m.alphabet.size();
        // Directions
        dir = new int[n];
        Arrays.fill(dir, 0);
        // States
        next = new State[n];
        Arrays.fill(next, this);
        // writes
        write = m.alphabet.getCharArray();
    }

    public void setLine(char read, char write, Direction d, String name) {
        int i = machine.alphabet.getIndex(read);
        this.write[i] = write;
        dir[i] = d.getOffset();
        next[i] = machine.states.get(name);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        char[] read = machine.alphabet.getCharArray();
        for (int i=0; i<write.length; i++) {
            sb.append("\t").append(read[i]).append("\t").append(write[i]).append("\t").append(dir[i]).append("\t");
            if (next[i]!=null) {
                sb.append(next[i].name);
            } else {
                sb.append("next is null!!!");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    State exec(Tape tape, LineFilter lf) {
        char r = tape.read();
        int read = machine.alphabet.getIndex(r);
        tape.write(write[read]);
        tape.move(dir[read]);
        lf.read = r;
        lf.write = write[read];
        lf.oldState = lf.stateName;
        lf.stateName = this.name;
        lf.dirOffset = dir[read];
        lf.pos = tape.getRealPos();
        return next[read];
    }

    void translateAlphabet(String src, String dest) {
        for (int i=0; i<write.length; i++) {
            int p = src.indexOf(""+write[i]);
            if (p>=0) {
                write[i] = dest.charAt(p);
            }
        }
    }
    
}
