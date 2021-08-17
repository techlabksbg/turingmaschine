/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import java.util.ArrayList;
import tm.TM;
import tm.machine.Alphabet;
import tm.machine.Machine;

/**
 *
 * @author ivo
 */
public class State {
    public StateHeader header;
    int firstLine = 0;
    int lastLine = 0;
    ArrayList<StateLine> stateLines = new ArrayList<>();
    
    State(String[] lines, int line, int endLine) {
        while (line<endLine && (lines[line].isEmpty()) || (lines[line].startsWith("#"))) line++;
        if (line==endLine) {
            throw new IllegalArgumentException(String.format("Reached end while expecting State description, starting at line %d: %s", line, lines[line]));
        }
        firstLine = line;
        header = new StateHeader(lines[line], line);
        line++;
        while (line<endLine && (!lines[line].isEmpty())) {
            if (!lines[line].startsWith("#")) {
                try {
                    stateLines.add(new StateLine(lines[line],line));
                } catch (IllegalArgumentException e) {
                   throw new IllegalArgumentException(String.format("Error when trying to parse StateLine in line %d: %s",line, lines[line]),e);
                }
            }
            line++;
        }
        lastLine = line;
    }
    
    public void translate(String src, String dest) {
        for (StateLine sl : stateLines) {
            sl.translate(src, dest);
        }
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(header.toString()).append("\n");
        for (StateLine sl : stateLines) {
            sb.append("   ").append(sl.toString()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    void declare(Parser parser, Machine machine, Scope scope) {
        String name = header.name.getScopedName(scope);
        if (TM.verbose) System.out.format("Declaring state %s\n",name);
        machine.declare(name, header.defaultDir);
    }
    
    private String getNextStateName(Parser parser, Machine machine, Scope scope, StateName nextState) {
        Scope nextScope = scope;
        String next = nextState.getName(scope, machine);
        if (TM.verbose) System.out.println("next="+next+" nextState="+nextState);
        if (nextState.isMCall) {
            if (!machine.states.exists(next)) {
                // Implement all SubMCalls first
                for (StateName sn : nextState.mCall.stateNames) {
                    if (sn.isMCall) {
                        getNextStateName(parser, machine, scope, sn);
                    }
                }
                Scope subScope = new Scope(nextState.mCall, scope, machine);                
                MFunction mf = parser.mFunctions.get(nextState.mCall.name);
                if (mf==null) {
                    throw new IllegalArgumentException(String.format("m-function for m-call %s at line %d does not exist!",nextState.mCall.toString(), nextState.mCall.line));
                }
                if (mf.header.numSymbols != subScope.symbols.length) {
                    throw new IllegalArgumentException(String.format("Wrong number of symbols in line %d for call %s of MFunction %s defined in line %d",
                            nextState.line, nextState.toString(), mf.toString(), mf.firstLine));
                }
                if (mf.header.numStates != subScope.states.size()) {
                    throw new IllegalArgumentException(String.format("Wrong number of states in line %d for call %s of MFunction %s defined in line %d",
                            nextState.line, nextState.toString(), mf.toString(), mf.firstLine));
                }
                machine.declare(next);
                try {
                    parser.declareAndImplement(mf.body.states, subScope, machine);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Error while instanciating m-function %s called in line %d with %s",
                            mf.header.toString(), nextState.line, nextState.mCall.toString()),e);
                }
                // Link 
                machine.states.link(next,mf.body.states.get(0).header.name.getName(subScope,machine));
            }  
        } else {
            return nextState.getName(scope,machine);
        }
        return next;
    }
    
    void implement(Parser parser, Machine machine, Scope scope) {
        String stateName = header.name.getScopedName(scope);
        if (TM.verbose) System.out.format("Implementing state %s\n",stateName);
        tm.machine.State tmState = machine.states.get(stateName);
        // Set default state
        String defState = getNextStateName(parser, machine, scope, header.defaultNextState);
        if (defState==null) {
            throw new IllegalArgumentException(String.format("Error: Could not find default state %s in line %d%n",header.defaultNextState.name,this.firstLine));
        }
        if (TM.verbose) System.out.format("  setting default state to %s\n",defState);
        tmState.setDefaultState(defState);
        // Implement StateLines
        for (StateLine l : stateLines) {
            char[] reads;
            if (l.read.isStar) {
                reads = machine.alphabet.getCharArray(); 
            } else if (l.read.isRange) {
                reads = l.read.getRange();
            } else {
                reads = new char[]{l.read.getSymbol(scope)};
            }
            for (char read : reads) {
                scope.wildcard = read;
                if (TM.verbose) System.out.format("   Implementing line %s with read=%c\n", l.toString(), read);
                String next = getNextStateName(parser, machine, scope, l.nextState);
                if (machine.states.get(next)==null) {
                    throw new IllegalArgumentException(String.format("Error while parsing state line %s at line %d: State %s is not defined.",l.toString(),l.line, next));
                }  
                if (TM.verbose) System.out.format("   Setting next state in line %s with read=%c to %s\n", l.toString(), read, next);
            // simple lines
                if (l.isSimple) {
                    char write = l.write.getSymbol(scope);
                    Direction d = l.dir;
                    tmState.setLine(read, write, d, next);
                } else { // command lines
                                      
                    try {
                        machine.makeCommandStates(tmState, read, l.commands, machine.states.get(next), scope);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(String.format("Error while expanding commands %s at line %d",l.toString(),l.line),e);
                    }
                }
                scope.wildcard = 0;
            }
        }
    }

    void addTo(Alphabet a) {
        for (StateLine sl: stateLines) {
            sl.addTo(a);
        }
    }
    
    
}
