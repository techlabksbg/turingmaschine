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
public class StateChangeLineFilter extends LineFilter {
    String oldState;
    
    @Override
    public boolean isInteresting() {
        boolean res = (oldState!=stateName);
        oldState = stateName;
        if (other!=null) {
            return getOther() && res;
        }
        return res;
    }
}
