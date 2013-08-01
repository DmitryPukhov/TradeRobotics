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
public class BarEntity  {
    private Bar relativeBar;
    private Bar absoluteBar;
    

    /**
     * Construct from current and previous bars
     * @param absolute
     * @param relative 
     */
    public BarEntity(Bar current, Bar previous){
        absoluteBar = current;
        // Calculate relative value from current and previous bar
        relativeBar = new Bar(absoluteBar.getTime(), 
                absoluteBar.getOpen()/previous.getOpen() - 1,
                absoluteBar.getHigh()/previous.getHigh() - 1,
                absoluteBar.getLow()/previous.getLow() - 1,
                absoluteBar.getClose()/previous.getClose() - 1,
                absoluteBar.getVolume()/previous.getVolume() - 1
                );
    }
    
    /**
     * Time of the bar
     * @return 
     */
    public Calendar getTime(){
        return absoluteBar.getTime();
    }
    
    /**
     * Get bar with percentages
     * @return 
     */
    public Bar getRelativeBar() {
        return relativeBar;
    }

    public void setRelativeBar(Bar relativeValue) {
        this.relativeBar = relativeValue;
    }

    public Bar getAbsoluteBar() {
        return absoluteBar;
    }

    public void setAbsoluteBar(Bar absoluteValue) {
        this.absoluteBar = absoluteValue;
    }

    
    
    
}
