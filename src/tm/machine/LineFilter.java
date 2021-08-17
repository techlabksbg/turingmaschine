/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 *
 * @author ivo
 */
public class LineFilter {
    public char read;
    public char write;
    public int pos;
    public int dirOffset;
    public long iter;
    public String stateName;
    public String oldState;
    private ArrayList<Predicate<LineFilter>> filters = new ArrayList<>();
    
    public void add(Predicate<LineFilter> filter) {
        filters.add(filter);
    }
    
    public boolean use() {
        boolean use = true;
        return filters.stream().allMatch((p) -> (p.test(this)));
    }
    
    
}
