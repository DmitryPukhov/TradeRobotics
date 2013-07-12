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
        assertEquals("Low price", bars.get(1).getLow(), low, 0);        
        assertEquals("High price", bars.get(1).getHigh(), high, 0);        
    }
//    /**
//     * Test of createBufferedMLDataSet method, of class MLDataSetLoader.
//     */
//    @Test
//    public void testCreateBufferedMLDataSet() throws Exception {
//        System.out.println("createBufferedMLDataSet");
//        MLDataSet expResult = null;
//        MLDataSet result = MLDataSetLoader.createBufferedMLDataSet();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of createBufferedMLDataSet method, of class MLDataSetLoader.
//     */
//    @Test
//    public void testCreateBufferedMLDataSet_3args() throws Exception {
//        System.out.println("createBufferedMLDataSet");
//        List<Bar> smallBars = null;
//        List<Bar> mediumBars = null;
//        List<Bar> largeBars = null;
//        MLDataSet expResult = null;
//        MLDataSet result = MLDataSetLoader.createBufferedMLDataSet(smallBars, mediumBars, largeBars);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    
}