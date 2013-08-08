/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Output of neural network.  
 * @author pdg
 */
public class OutputEntity {
    private BarEntity currentBar;
    private Calendar futureTime;
    private double futureRelativeHigh;
    private double futureRelativeLow;

    /**
     * Private constructor. Calls only from static methods
     */
    private OutputEntity(){
        
    }
    
    
    /**
     * Construct from relative high, low values
     * @param bar
     * @param futureIntervalMillis prediction interval from current bar
     * @param futureRelativeHigh
     * @param futureRelativeLow 
     */
    public static OutputEntity createFromRelativeData(BarEntity bar, long futureIntervalMillis, double futureRelativeHigh, double futureRelativeLow) {
        // Calculate future time
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(bar.getTime().getTimeInMillis());
        cal.add(Calendar.MILLISECOND, (int)futureIntervalMillis);
        // Create a new entity
        OutputEntity output = new OutputEntity();
        output.currentBar = bar;
        output.futureTime = cal;
        output.futureRelativeHigh = futureRelativeHigh;
        output.futureRelativeLow = futureRelativeLow;
        return output;
    }
    
    /**
     * Construct from absolute high, low values
     * @param bar
     * @param futureIntervalMillis prediction interval from current bar
     * @param futureAbsoluteHigh
     * @param futureAbsoluteLow
     * @return 
     */
    public static OutputEntity createFromAbsoluteData(BarEntity bar, long futureIntervalMillis, double futureAbsoluteHigh, double futureAbsoluteLow){
        return createFromRelativeData(bar, futureIntervalMillis, 1-futureAbsoluteHigh/bar.getAbsoluteBar().getHigh(), 1-futureAbsoluteLow/bar.getAbsoluteBar().getLow());
    }
    
    /**
     * Get current, not future bar
     * @return 
     */
    public BarEntity getCurrentBarEntity() {
        return currentBar;
    }
    
    
    /**
     * Prediction bar close time
     * @return 
     */
    public Calendar getFutureTime() {
        return futureTime;
    }
    /**
     * Get prediction interval, milliseconds between future and current moment
     * @return 
     */
    public long getFutureIntervalMillis(){
        return futureTime.getTimeInMillis() - currentBar.getTime().getTimeInMillis();
    }
    
    
    /**
     * High change percent
     * @return 
     */
    public double getFutureRelativeHigh() {
        return futureRelativeHigh;
    }

    /**
     * Low change percent
     * @return 
     */
    public double getFutureRelativeLow() {
        return futureRelativeLow;
    }
    /**
     * Gets absolute value of High price
     * @return 
     */
    public double getFutureAbsoluteHigh(){
        return currentBar.getAbsoluteBar().getHigh() * (1.0+futureRelativeHigh);
    }
     /**
     * Gets absolute value of Low price
     * @return 
     */
    public double getFutureAbsoluteLow(){
        return currentBar.getAbsoluteBar().getLow() * (1.0+futureRelativeLow);
    }   
}
