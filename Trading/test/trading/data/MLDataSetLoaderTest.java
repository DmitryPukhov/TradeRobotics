/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.encog.ml.data.MLData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pdg
 */
public class MLDataSetLoaderTest {
    
    public MLDataSetLoaderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        // Add test bars
        Date time0 = new Date();
        
        // 15 min
        Calendar time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 15);
        bars.add(new Bar(time,2,1,4,3,10));
        
        // 30 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 30);
        bars.add(new Bar(time,12,11,14,13,110));
        
        // 45 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 45);
        bars.add(new Bar(time,22,21,24,23,210));     
        
        // 60 min
        time = GregorianCalendar.getInstance();
        time.setTime(time0); time.add(Calendar.MINUTE, 45);
        bars.add(new Bar(time,32,31,34,33,310));           
    }
    
    @After
    public void tearDown() {
    }
    /**
     * Bars for loading to dataset
     */
    private List<Bar> bars = new ArrayList<>();

    
    
    @Test
    public void testGetOutputData() throws Exception{
        System.out.println("getOutputData");
        // Invoke private method using reflection
        Method method = MLDataSetLoader.class.getDeclaredMethod("getOutputData", List.class, Integer.TYPE, Calendar.class); //(List<Bar> bars, int pos, Calendar currentTime 
        method.setAccessible(true);
        MLData data = (MLData) method.invoke(null, bars, 0, bars.get(0).getTime());
        // Check not null
        assertNotNull( data);
        assertEquals("MLData size", 2, data.size());
        
        // Check results
        double high = data.getData(0);
        double low = data.getData(1);
        assertEquals("Low price", bars.get(1).getLow()/bars.get(0).getLow() - 1, low, 0);        
        assertEquals("High price", bars.get(1).getHigh()/bars.get(0).getHigh() - 1, high, 0);        
    }
    
    /**
     * barsToMLData conversion test
     * @throws Exception 
     */
    @Test
    public void testBarsToMLData() throws Exception{
        List<Bar> prevBars = getPrevBars(bars);
        System.out.println("testBarsToMLData");
        MLDataSetLoader dsl = new MLDataSetLoader();
        // Invoke method
        Method method = MLDataSetLoader.class.getDeclaredMethod("barsToMLData", List.class, List.class);
        method.setAccessible(true);
        MLData data = (MLData) method.invoke(dsl,bars, prevBars);
        
        // Check the result
        for(int i = 0; i < bars.size(); i++){
            assertEquals(0.1, data.getData(i*Bar.FIELD_COUNT+1),0.000000001);
            assertEquals(0.2, data.getData(i*Bar.FIELD_COUNT+2),0.000000001);
            assertEquals(0.3, data.getData(i*Bar.FIELD_COUNT+3),0.000000001);
            assertEquals(0.4, data.getData(i*Bar.FIELD_COUNT+4),0.000000001);
            assertEquals(0.5, data.getData(i*Bar.FIELD_COUNT+5),0.000000001);
        }
    }
    
    /**
     * Get nearest by time bar pos test
     * @throws Exception 
     */
    @Test
    public void testGetNearestPos() throws Exception{
        // Invoke method
        Method method = MLDataSetLoader.class.getDeclaredMethod("getNearestPos", List.class, Calendar.class, Integer.TYPE);
        method.setAccessible(true); 
        int pos = (int)method.invoke(null,bars, bars.get(0).getTime(),0);
        assertEquals(pos,1);
        
        pos = (int)method.invoke(null,bars, bars.get(1).getTime(),0);
        assertEquals(pos,2);
        
        // -1 if no bars above
        pos = (int)method.invoke(null,bars, bars.get(bars.size()-1).getTime(),bars.size()-1);
        assertEquals(pos,-1);        
        
    }
  /**
     * Get last bar before the time
     * @throws Exception 
     */
    @Test
    public void testGetLastPos() throws Exception{
        // Invoke method
        MLDataSetLoader dsl = new MLDataSetLoader();
        Method method = MLDataSetLoader.class.getDeclaredMethod("getLastPos", List.class, Calendar.class, Integer.TYPE);
        method.setAccessible(true); 
        int pos = (int)method.invoke(dsl,bars, bars.get(1).getTime(),1);
        assertEquals(pos,1);
        
    }    
    /**
     * Util method - get last bars from bars
     * @return 
     */
    public List<Bar> getPrevBars(List<Bar> bars){
       
       List<Bar> prevBars = new ArrayList<>();

       for(Bar bar: bars){
           Bar prevBar = new Bar(bar.getTime(), bar.getOpen()/1.1, bar.getHigh()/1.2, bar.getLow()/1.3, bar.getClose()/1.4, bar.getVolume()/1.5);
           prevBars.add(prevBar);
       }
       return prevBars;
    }
    
    
}