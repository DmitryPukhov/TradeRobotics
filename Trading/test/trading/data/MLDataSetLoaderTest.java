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
    
    //<editor-fold desc="Test creation and closure">
    public MLDataSetLoaderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception{
        // Create and transform bars
        bars = createNewBars();      
        transformBars(bars);
    }
    
    /**
     * Util method transform bars from linear to change percent values
     * @param bars 
     */
    private static void transformBars(List<Bar> bars) throws Exception{
        // Call data seet loader transform method
        Method method = MLDataSetLoader.class.getDeclaredMethod("transformBars", List.class); 
        method.setAccessible(true);
        method.invoke(null, bars);      
    }
    
    /**
     * Util method
     * Create bar list for test.
     * Bars are not transformed
     */
    private static List<Bar> createNewBars(){
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
            
    
    @After
    public void tearDown() {
    }
    
    //</editor-fold>
    
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
        assertEquals("Low price", bars.get(1).getLow(), low, 0);        
        assertEquals("High price", bars.get(1).getHigh(), high, 0);        
    }
    
    /**
     * barsToMLData conversion test
     * @throws Exception 
     */
    @Test
    public void testBarsToMLData() throws Exception{
        System.out.println("testBarsToMLData");
        MLDataSetLoader dsl = new MLDataSetLoader();
        // Invoke method
        Method method = MLDataSetLoader.class.getDeclaredMethod("barsToMLData", List.class);
        method.setAccessible(true);
        MLData data = (MLData) method.invoke(dsl,bars);
        
        // Check the result
        for(int i = 0; i < bars.size(); i++){
            Bar bar = bars.get(i);
            assertEquals(bar.getTime().getTimeInMillis(), data.getData(i*Bar.FIELD_COUNT),0.000000001);
            assertEquals(bar.getOpen(), data.getData(i*Bar.FIELD_COUNT+1),0.000000001);
            assertEquals(bar.getHigh(), data.getData(i*Bar.FIELD_COUNT+2),0.000000001);
            assertEquals(bar.getLow(), data.getData(i*Bar.FIELD_COUNT+3),0.000000001);
            assertEquals(bar.getClose(), data.getData(i*Bar.FIELD_COUNT+4),0.000000001);
            assertEquals(bar.getVolume(), data.getData(i*Bar.FIELD_COUNT+5),0.000000001);
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
}