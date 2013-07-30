/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

import java.util.Calendar;

/**
 * Output of neural network
 * @author pdg
 */
public class RealOutputEntity {
    private Bar previousBar;
    private Calendar time;
    private double relativeHigh;
    private double relativeLow;

    /**
     * Construct from values
     * @param previousBar
     * @param time
     * @param relativeHigh
     * @param relativeLow 
     */
    public RealOutputEntity(Bar previousBar, Calendar time, double relativeHigh, double relativeLow) {
        this.previousBar = previousBar;
        this.time = time;
        this.relativeHigh = relativeHigh;
        this.relativeLow = relativeLow;
    }
    
    
    /**
     * Bar close time
     * @return 
     */
    public Calendar getTime() {
        return time;
    }
    
    /**
     * Bar before this. Relative value is change percent from this bar.
     * @return 
     */
    public Bar getPreviousBar() {
        return previousBar;
    }
    
    /**
     * High change percent
     * @return 
     */
    public double getRelativeHigh() {
        return relativeHigh;
    }

    /**
     * Low change percent
     * @return 
     */
    public double getRelativeLow() {
        return relativeLow;
    }
    /**
     * Gets absolute value of High price
     * @return 
     */
    public double getAbsoluteHigh(){
        return previousBar.getHigh() * (1.0+relativeHigh);
    }
     /**
     * Gets absolute value of Low price
     * @return 
     */
    public double getAbsoluteLow(){
        return previousBar.getLow() * (1.0+relativeLow);
    }   
}
