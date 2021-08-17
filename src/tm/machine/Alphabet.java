/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author ivo
 */
public class Alphabet  implements Iterable<Character> {
    private final HashMap<Character,Integer> map = new HashMap<>();
    private final ArrayList<Character> list = new ArrayList<>();
    
    public Alphabet() {
        add('.');
    }
    
    public Alphabet(Tape tape) {
        this();
        for (char c : tape.toString().toCharArray()) {
            add(c);
        }
    }
    
    public void add(String chars) {
        for (char c: chars.toCharArray()) {
            add(c);
        }
    }
    
    public final void add(char symbol) {
        if (!map.containsKey(symbol)) {
            map.put(symbol, map.size());
            list.add(symbol);
        }
    }
    
    public boolean contains(char symbol) {
        return map.containsKey(symbol);
    }
    
    public int getIndex(char symbol) {
        if (!map.containsKey(symbol)) {
            throw new IllegalArgumentException(String.format("'%c' is not in the Alphabet: %s\n",symbol,this));
        }
        return map.get(symbol);
    }
    
    public int size() {
        return list.size();
    }

    @Override
    public Iterator<Character> iterator() {
        return list.iterator();
    }    
    
    @Override
    public String toString() {
        return new String(getCharArray());
    }

    public char[] getCharArray() {
        int n = size();
        char[] r = new char[n];
        for (int i=0; i<n; i++) {
            r[i] = list.get(i);
        }
        return r;
    }

    void translate(String src, String dest) {
        map.clear();
        for (int i=0; i<list.size(); i++) {
            int p = src.indexOf(""+list.get(i));
            if (p>=0) {
                list.set(i, dest.charAt(p));
            }
            map.put(list.get(i), i);
        }
    }
    
}
