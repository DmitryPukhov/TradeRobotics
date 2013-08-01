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
    private Bar previousAbsoluteBar;
    

    /**
     * Construct from current and previous bars
     * @param absolute
     * @param relative 
     */
    public BarEntity(Bar current, Bar previous){
        absoluteBar = current;
        // Calculate relative value from current and previous bar
        relativeBar = new Bar(absoluteBar.getTime(), 
                (absoluteBar.getOpen() - previous.getOpen())/previous.getOpen(),
                (absoluteBar.getHigh() - previous.getHigh())/previous.getHigh(),
                (absoluteBar.getLow() - previous.getLow())/previous.getLow(),
                (absoluteBar.getClose() - previous.getClose())/previous.getClose(),
                (absoluteBar.getVolume() - previous.getVolume())/previous.getVolume()
                );
        previousAbsoluteBar = previous;
    }
    
    /**
     * Time of the bar
     * @return 
     */
    public Calendar getTime(){
        return absoluteBar.getTime();
    }
    
    /**
     * Gets previous absolute bar
     * @return 
     */
    public Bar getPreviousAbsoluteBar() {
        return previousAbsoluteBar;
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
