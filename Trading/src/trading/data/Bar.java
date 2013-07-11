/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Bar model class
 * @author pdg
 */
public class Bar {
    private Calendar time;
    private double open;
    private double low;
    private double high;
    private double close;
    private double volume;
    
    public Bar(Calendar theTime, double theOpen, double theLow, double theHigh, double theClose, double theVolume){
        time = theTime; open = theOpen; low = theLow; high = theHigh; close = theClose; volume = theVolume;
    }
    
    public Calendar getTime() {
        return time;
    }


    public void setTime(Calendar time) {
        this.time = time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

}
