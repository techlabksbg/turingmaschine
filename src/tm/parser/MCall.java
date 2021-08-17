/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tm.machine.Machine;

/**
 *
 * @author ivo
 */
public class MCall {
    
    private static final Pattern MCALL_REGEXP = Pattern.compile("^@([^(]+)\\(\\s*([^;]*)\\s*;\\s*(.*)\\)$");
    String call;
    String name;
    Symbol[] symbols;
    ArrayList<StateName> stateNames = new ArrayList<>();
    int line;
    
    MCall(String call, int l) {
        this.call = call;
        this.line = l;
        Matcher m = MCALL_REGEXP.matcher(call);
        if (m.find()) {
            MatchResult mr=m.toMatchResult();
            this.name = mr.group(1);
            String[] s = mr.group(2).split("\\s+");
            int numSym = 0;
            for (int i=0; i<s.length; i++) {
                if (!s[i].isEmpty()) {
                    numSym++;
                }
            }
            symbols = new Symbol[numSym];
            numSym = 0;
            for (int i=0; i<s.length; i++) {
                if (!s[i].isEmpty()) {
                    symbols[numSym++] = new Symbol(s[i]);
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("Failed to parse MCall %s\n", call));
        }

        parseArgumentStates(m.group(3));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(name).append("(");
        for (int i=0; i<symbols.length; i++) {
            sb.append(symbols[i].toString());
            if (i<symbols.length-1) {
                sb.append(" ");
            }
        }
        sb.append(";");
        for (int i=0; i<stateNames.size(); i++) {
            sb.append(stateNames.get(i).toString());
            if (i<stateNames.size()-1) sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }
        
    private void parseArgumentStates(String l) {
        int p = 0;
        while (p<l.length()) {
            if (l.charAt(p)=='@') { // mCall
                int pp=p+1;
                while (pp<l.length() && l.charAt(pp)!='(') pp++;
                if (pp==l.length()) {
                    throw new IllegalArgumentException(String.format("Error parsing argument states, expected opening '(' %s%n",l));
                }
                int open=1;
                pp++;
                while (pp<l.length() && open>0) {
                    if (l.charAt(pp)=='(') open++;
                    if (l.charAt(pp)==')') open--;
                    pp++;
                }
                if (pp==l.length() && open>0) {
                    throw new IllegalArgumentException(String.format("Error parsing argument states, expected closing ')' %s%n",l));
                }
                stateNames.add(new StateName(l.substring(p,pp),this.line));
                p = pp-1;
            } else {
                if (l.charAt(p)!=' ') {
                    int pp=p+1;
                    while (pp<l.length() && l.charAt(pp)!=' ' && l.charAt(pp)!=')') pp++;
                    stateNames.add(new StateName(l.substring(p, pp), this.line));
                    p = pp-1;
                }
            }
            p++;
        }
    }

    public String getName(Scope scope, Machine m) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("_");
        for (Symbol s:symbols) {
            sb.append(s.getSymbol(scope));
        }
        sb.append("__");
        for (StateName s : stateNames) {
            sb.append(s.getName(scope,m)).append("_");
        }
        return sb.toString();
    }

    void translate(String src, String dest) {
        for (Symbol s : symbols) {
            s.translate(src, dest);
        }
        for (StateName s : stateNames) {
            s.translate(src, dest);
        }
    }

   
    
}
