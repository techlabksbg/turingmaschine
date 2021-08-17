/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import tm.parser.StateHeader;

/**
 *
 * @author ivo
 */
public class States extends HashMap<String, State> {

    public States(Machine m) {
        put("stop", new State(m));
    }
    
    public boolean exists(String stateName) {
        return containsKey(stateName);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<State> names = getList();
        Comparator<State> comp = new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.name.compareTo(o2.name);
            }
        };
        Collections.sort(names,comp);
            
        for (State s : names) {
            sb.append(s.toString());
        }
        return sb.toString();
    }
    
    public ArrayList<State> getList() {
        ArrayList<State> states = new ArrayList<>();
        State stop = get("stop");
        int id=0;
        for (State s : values()) {
            if (!states.contains(s) && s!=stop) {
                s.id = id++;
                states.add(s);
            }
        }
        return states;
    }

    public void link(String src, String dest) {
        put(src,get(dest));
    }
    
}
