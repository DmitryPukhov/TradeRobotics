/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import trading.data.model.Bar;
import trading.data.model.BarEntity;

/**
 *
 * @author pdg
 */
public class TestUtilities {
    
    
    /**
     * Util method transform bars from linear to change percent values
     * @param bars 
     */
    public static List<BarEntity> getBarEntities(List<Bar> bars) throws Exception{
        // Call data seet loader transform method
        Method method = MLBarDataLoader.class.getDeclaredMethod("getBarEntities", List.class); 
        method.setAccessible(true);
        List<BarEntity> result = (List)method.invoke(null, bars);      
        return result;
    }
    
    /**
     * Util method
     * Create bar list for test.
     * Bars are not transformed
     */
    public static List<Bar> createLinearBars(){
        ArrayList<Bar> newBars = new ArrayList();
        // Add test bars
        Date time0 = new Date();
        
        // 15 min
        Calendar time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 15);
        newBars.add(new Bar(time,2,1,4,3,10));
        
        // 30 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 30);
        newBars.add(new Bar(time,12,11,14,13,110));
        
        // 45 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 45);
        newBars.add(new Bar(time,22,21,24,23,210));     
        
        // 60 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 45);
        newBars.add(new Bar(time,32,31,34,33,310));   
        
        return newBars;
    }    
    

    

}
