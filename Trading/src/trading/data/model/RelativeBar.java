/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

import java.util.Calendar;

/**
 * Bars with absolute price data and percent change from previous bar
 * @author dima
 */
public class RelativeBar  {
    private Bar relativeValue;
    private Bar absoluteValue;
    

    /**
     * Construct from current and previous bars
     * @param absolute
     * @param relative 
     */
    public RelativeBar(Bar current, Bar previous){
        absoluteValue = current;
        // Calculate relative value from current and previous bar
        relativeValue = new Bar(absoluteValue.getTime(), 
                absoluteValue.getOpen()/previous.getOpen() - 1,
                absoluteValue.getHigh()/previous.getHigh() - 1,
                absoluteValue.getLow()/previous.getLow() - 1,
                absoluteValue.getClose()/previous.getClose() - 1,
                absoluteValue.getVolume()/previous.getVolume() - 1
                );
    }
    
    /**
     * Time of the bar
     * @return 
     */
    public Calendar getTime(){
        return absoluteValue.getTime();
    }
    
    /**
     * Get bar with percentages
     * @return 
     */
    public Bar getRelativeValue() {
        return relativeValue;
    }

    public void setRelativeValue(Bar relativeValue) {
        this.relativeValue = relativeValue;
    }

    public Bar getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(Bar absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    
    
    
}
