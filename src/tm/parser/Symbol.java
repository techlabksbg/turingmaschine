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
public class Symbol {
    boolean isVariable = false;
    boolean isStar = false;
    boolean isRange = false;

//    public static Alphabet alphabet;
    
    private char symbol = 0;
    private int num = 0;
    private char[] range = null;
    
    Symbol(String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Error: Empty Symbol!");
        }
        if (s.length()==1) {
            symbol = s.charAt(0);
            if (symbol=='*') {
                isStar = true;
            } else {
                if ("#@_$[]".contains(s)) {
                    throw new IllegalArgumentException(String.format("Error: Symbol %s is not allowed!",s));
                }
                //alphabet.add(symbol);
            }
        } else {
            if (s.charAt(0)=='@') {
                isVariable = true;
                num = Integer.parseInt(s.substring(1));
            } else if (s.charAt(0)=='[') {
                range = s.replaceAll("[\\[\\]\\s]", "").toCharArray();
                isRange = true;
            } else {
                throw new IllegalArgumentException(String.format("Error: %s is not a symbol!",s));
            }
        }
    }
    
    public char getSymbol(Scope scope) {
        if (isVariable) {
            if (scope.symbols==null || num-1>=scope.symbols.length) {
                throw new IllegalArgumentException("Symbol "+this+" is not defined. length="+scope.symbols.length);
            }
            return scope.symbols[num-1];
        }
        if (isStar) {
            if (scope.wildcard!=0) {
                return scope.wildcard;
            }
            throw new IllegalArgumentException("cannot get symbol for '*'");
        }
        return symbol;
    }
   
    @Override
    public String toString() {
        if (isVariable) {
            return "@"+num;
        }
        if (isRange) {
            return "["+(new String(range))+"]";
        }
        return ""+symbol;            
    }

    char[] getRange() {
        return range;
    }
    
    void translate(String src, String dest) {
        if (!isVariable && !isRange) {
            int p = src.indexOf(symbol);
            if (p>=0) {
                symbol = dest.charAt(p);
            }
        }
    }

    void addTo(Alphabet a) {
        if (!(isVariable || isRange || isStar)) {
            a.add(symbol);
        }
    }

}
