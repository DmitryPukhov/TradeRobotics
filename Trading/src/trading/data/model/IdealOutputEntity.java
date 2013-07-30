/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Output entity to form ideal MLData and compare with real output
 * @author dima
 */
public class IdealOutputEntity {
    private RelativeBar bar;
    
    /**
     * Construct from bar
     * @param bar 
     */
    public IdealOutputEntity(RelativeBar bar){
        this.bar = bar;
    }
    /**
     * Bar to get prediction values from.
     * @return 
     */
    public RelativeBar getBar() {
        return bar;
    }
    
    /**
     * Gets minimum price
     * @return 
     */
    public double getAbsoluteLow(){
        return bar.getAbsoluteValue().getLow();
    }
    /**
     * Gets maximum price
     * @return 
     */
    public double getAbsoluteHigh(){
        return bar.getAbsoluteValue().getHigh();
    }
}
