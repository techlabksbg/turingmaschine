/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm.machine;

/**
 *
 * @author ivo
 */
public class TapeRangeLineFilter extends LineFilter {

    private int min;
    private int max;
    public TapeRangeLineFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public boolean isInteresting() {
        return getOther() && this.pos>=min && this.pos<=max;
    }
    
}
