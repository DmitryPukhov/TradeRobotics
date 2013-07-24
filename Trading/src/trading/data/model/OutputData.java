/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Output entities
 * @author dima
 */
public class OutputData {
    private RelativeBar bar;
    
    /**
     * Construct from bar
     * @param bar 
     */
    public OutputData(RelativeBar bar){
        this.bar = bar;
    }
    /**
     * Bar to get prediction values from.
     * @return 
     */
    public RelativeBar getBar() {
        return bar;
    }
}
