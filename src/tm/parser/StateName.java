/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import tm.machine.Machine;

/**
 *
 * @author ivo
 */
public class StateName {
    boolean isMCall = false;
    boolean isVariable = false;
    String name;
    MCall mCall = null;
    int num = 0;
    int line = 0;
    
    public StateName(String s, int line) {
        this.line = line;
        name = s;
        if (s.startsWith("@")) { // Mcall
            isMCall = true;
            mCall = new MCall(s, line);
        } else if (s.startsWith("$")) {
            isVariable = true;
            try {
                num = Integer.parseInt(s.substring(1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("This is not a proper variable state: %s in line %d",s, line));
            }
        } else {
            if (s.matches(".*[ @$_*\\[\\]\\(\\)].*")) {
                throw new IllegalArgumentException(String.format("This is not a proper state name: %s in line %d",s, line));
            }
        }
    }
    
    public String getName(Scope scope, Machine m) {
        if (isMCall) {
            return mCall.getName(scope, m);
        }
        if (isVariable) {
            if (scope.states.size()<=num-1) {
                throw new IllegalArgumentException(String.format("Variable $%d not defined at line %d!",num,line));
            }
            String s = scope.states.get(num-1);
            if (m.states.exists(s)) {
                return s;
            }
            return scope.prefix+s;
        }
        if (m.states.exists(scope.prefix+name)) {
            return scope.prefix+name;
        }
        return name;
    }
    
    
    @Override
    public String toString() {
        if (isVariable) {
            return "$"+num;
        }
        if (isMCall) {
            return mCall.toString();
        }
        return name;
    }

    String getScopedName(Scope scope) {
        return scope.prefix+name;
    }

    void translate(String src, String dest) {
        if (isMCall) {
            mCall.translate(src, dest);
        }
    }
}
