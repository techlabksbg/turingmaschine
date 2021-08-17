/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import tm.Options;
import tm.TM;
import tm.parser.Command;
import tm.parser.Direction;
import tm.parser.Scope;

/**
 *
 * @author ivo
 */
public class Machine {
    public States states;
    public Alphabet alphabet;
    public Tape tape;
    
    public Machine(Alphabet alphabet, Tape tape) {
        this.alphabet = alphabet;
        this.tape = tape;
        states = new States(this);
    }
    /*
    public void declare(tm.parser.State state, Scope scope) {
        String name = scope.prefix+state.header.name.toString();
        if (!states.exists(name)) {
            states.put(name, new State(state.header, this, scope));
        }
    }*/
    
    public void declare(String name, Direction defaultDir) {
        states.put(name, new State(this, name, defaultDir));
    }
    
    public void declare(tm.parser.MCall call, Scope scope) {
        String name = call.getName(scope, this);
        if (!states.exists(name)) {
            states.put(name, new State(this, name));
        }
    }
    
    public void declare(String name) {
        states.put(name, new State(this, name));
    }
    
    public void makeCommandStates(State start, char read, Command[] commands, State end, Scope scope) {
        ArrayList<State> tempStates = new ArrayList<>();
        int i = 1;
        if (commands[0].write!=null) {
            i = 2;
        }
        for (; i<commands.length; i++) {
            State tmp = new State(this);
            tmp.name = String.format("%s_%c_%02d",start.name,read,i);
            if (commands[i].write!=null) {
                if (i<commands.length-1) {
                    Arrays.fill(tmp.dir,commands[i+1].dir.getOffset());
                } else {
                    Arrays.fill(tmp.dir,0);
                }
                Arrays.fill(tmp.write, commands[i].write.getSymbol(scope));
                i++;
            } else {
                Arrays.fill(tmp.dir,commands[i].dir.getOffset());
            }
            tempStates.add(tmp);
            states.put(tmp.name, tmp);
        }
        String nextState = end.name;
        if (tempStates.size()>0) {
            nextState = tempStates.get(0).name;
            Arrays.fill(tempStates.get(tempStates.size()-1).next,end);
            for (i=0; i<tempStates.size()-1;i++) {
                Arrays.fill(tempStates.get(i).next,tempStates.get(i+1));
            }
        }
        if (commands[0].write!=null) {
            start.setLine(read, commands[0].write.getSymbol(scope), commands[1].dir, nextState);
        } else {
            start.setLine(read, read, commands[0].dir, nextState);
        }
    }
        
    public void makeCommandStatesOld(State start, char read, Command[] commands, State end, Scope scope) {
        State[] temp = new State[commands.length];
        for (int i=temp.length-1; i>0; i--) {
            temp[i] = new State(this);
            temp[i].name = String.format("%s_%c_%02d",start.name,read,i);
            Arrays.fill(temp[i].dir,commands[i].dir.getOffset());
            if (commands[i].write!=null) {
                Arrays.fill(temp[i].write, commands[i].write.getSymbol(scope));
            }
            if (i<temp.length-1) {
                Arrays.fill(temp[i].next, temp[i+1]);
            } else {
                Arrays.fill(temp[i].next, end);
            }
            states.put(temp[i].name, temp[i]);
        }
        start.setLine(read, (commands[0].write==null)?read:commands[0].write.getSymbol(scope), commands[0].dir,temp[1].name);
    }
    
    public void execute(Tape tape, PrintStream out, Options o) {
        State current = states.get("start");
        State stop = states.get("stop");
        tape.setWidth(o.width);
        out.println(tape.niceString());
        State old = null;
        long iter = 0;
        while (current != stop && (o.limit==0 || iter<o.limit) && !(TM.stop)) {
            String oldState = current.name;
            current = current.exec(tape,o.lineFilter);
            iter++;
            o.lineFilter.iter = iter;
            if (o.lineFilter.use()) {
                out.print(tape.niceString());
                out.format("   %15d  %s %n",iter,oldState);
            }
        }
    }
    
    // Encode stop as .
    // All other states start at id 0!
    
    public String universalEncode() {
        if (alphabet.contains('>') || alphabet.contains('!')) {
            throw new IllegalArgumentException(String.format("Error: the machine contains > or ! as symbols. You must translate them first as they are special in the encoding of the machine! Alphabet: %s",alphabet));
        }
        StringBuilder t = new StringBuilder(">");
        State start = states.get("start");
        State stop = states.get("stop");
        char[] read = alphabet.getCharArray();
        char[] dirs = new char[]{'L', 'N', 'R'};
        for (State s : states.getList()) {
            t.append('>');
            t.append(s==start?'!':'.');
            for (int i=0; i<read.length; i++) {
                t.append(read[i]).append(s.write[i]);
                t.append(dirs[s.dir[i]+1]);
                if (s.next[i]!=stop) {
                    t.append(Integer.toBinaryString(s.next[i].id));
                } 
                t.append('.');
            }
        }
        t.append("......!");
        for (char c:tape.toString().toCharArray()) {
            t.append(c).append('.');
        }
        return t.toString();
    }

    public String universalEncode2() {
        if (alphabet.contains('>') || alphabet.contains('!')) {
            throw new IllegalArgumentException(String.format("Error: the machine contains > or ! as symbols. You must translate them first as they are special in the encoding of the machine! Alphabet: %s",alphabet));
        }
        StringBuilder t = new StringBuilder(">");
        State start = states.get("start");
        State stop = states.get("stop");
        char[] read = alphabet.getCharArray();
        char[] dirs = new char[]{'.', '0', '1'};
        for (State s : states.getList()) {
            t.append('>');
            t.append(s==start?'!':'.');
            for (int i=0; i<read.length; i++) {
                // if the parser has information about symbols never read in this part, use it!
                if (s.next[i]!=null) {
                    t.append(read[i]).append(s.write[i]);
                    t.append(dirs[s.dir[i]+1]);
                    if (s.next[i]!=stop) {
                        t.append(Integer.toBinaryString(s.next[i].id));
                    } 
                    t.append('.');
                }
            }
        }
        t.append("......!");
        for (char c:tape.toString().toCharArray()) {
            t.append(c);
        }
        return t.toString();
    }
    
    
    
    public void optimize() {
        // weed out states with all N movements
        State start = states.get("start");        
        State stop = states.get("stop");
        while (true) {
            ArrayList<State> stateList = states.getList();
            for (State s : stateList) {
                if (s!=stop) {
                    boolean allN = true;
                    for (int d : s.dir) {
                        if (d!=0) {
                            allN = false;
                            break;
                        }
                    }
                    if (allN) {
                        for (State s2 : stateList) {
                            if (s2!=stop && s2!=s) {
                                for (int i=0; i<s2.next.length; i++) {
                                    if (s2.next[i]==s) {
                                        int wIndex = alphabet.getIndex(s2.write[i]);
                                        s2.write[i] = s.write[wIndex];
                                        s2.next = s.next;
                                    }
                                }
                            }
                        }
                        states.keySet().stream().filter((key) -> (states.get(key)==s)).forEach((key) -> {
                            states.remove(key);
                        });
                        continue;
                    }
                }
            }
            break;
        }
        // then skip all N movements
        ArrayList<State> stateList = states.getList();
        for (State s : stateList) {
            for (int j=0; j<s.next.length; j++) {
                if (s.dir[j]==0) {
                    for (State s2 : stateList) {
                        if (s2!=stop && s2!=s) {
                            for (int i=0; i<s2.next.length; i++) {
                                if (s2.next[i]==s) {
                                    int wIndex = alphabet.getIndex(s2.write[i]);
                                    s2.write[i] = s.write[wIndex];
                                    s2.next = s.next;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void translateAlphabet(String src, String dest) {
        if (src.length()!=dest.length()) {
            throw new IllegalArgumentException("Translating Alphabete: Number of symbols in source and destination is not equal!");
        }
        for (State s : states.getList()) {
            s.translateAlphabet(src,dest);
        }
        alphabet.translate(src,dest);
    }
   
}
