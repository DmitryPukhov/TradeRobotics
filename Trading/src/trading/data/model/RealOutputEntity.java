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
    private BarEntity bar;
    private Calendar time;
    private double relativeHigh;
    private double relativeLow;

    /**
     * Construct from values
     * @param bar
     * @param time
     * @param relativeHigh
     * @param relativeLow 
     */
    public RealOutputEntity(BarEntity bar, Calendar time, double relativeHigh, double relativeLow) {
        this.bar = bar;
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
//    public Bar getBar() {
//        return bar;
//    }
    
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
        return bar.getAbsoluteBar().getHigh() * (1.0+relativeHigh);
    }
     /**
     * Gets absolute value of Low price
     * @return 
     */
    public double getAbsoluteLow(){
        return bar.getAbsoluteBar().getLow() * (1.0+relativeLow);
    }   
}
