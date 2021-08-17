/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.parser;

import java.util.ArrayList;
import tm.machine.Machine;

/**
 *
 * @author ivo
 */
public class Scope {
    public String  prefix="";
    public char[] symbols = null;
//    public ArrayList<StateName> states = null;
    public ArrayList<String> states = null;
    public char wildcard = 0;
    
    // Add a List of locally defined states. Those are returned with a prefix. All other states are assumed to exist in the root.
    
    public static final Scope root = new Scope();
    
    // Default, root scope
    private Scope() {}

    
    // Scope for a specific MCall
    Scope(MCall call, Scope parent, Machine m) {
        prefix = call.getName(parent,m);
        symbols = new char[call.symbols.length];
        for (int i=0; i<symbols.length; i++) {
            symbols[i] = call.symbols[i].getSymbol(parent);
        }
        states = new ArrayList<>();
        for (StateName sn : call.stateNames) {
            states.add(sn.getName(parent,m));
        }
    }
}
