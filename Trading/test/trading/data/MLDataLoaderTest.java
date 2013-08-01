/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import trading.data.model.Bar;
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
import trading.data.model.IdealOutputEntity;
import trading.data.model.BarEntity;

/**
 *
 * @author pdg
 */
public class MLDataLoaderTest {
    
    //<editor-fold desc="Test creation and closure">
    public MLDataLoaderTest() {
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
        bars = TestUtilities.createLinearBars();      
        barEntities = MLBarDataConverter.barsToEntities(bars);
    }
    

            
    
    @After
    public void tearDown() {
    }
    
    //</editor-fold>
    
    /**
     * Bars for loading to dataset
     */
    private List<Bar> bars = new ArrayList<>();
    private List<BarEntity> barEntities = new ArrayList<>();
//
//    /**
//     * Transform bars from linear to change percentage test
//     * @throws Exception 
//     */
//    @Test 
//    public void testGetRelativeBars() throws Exception{
//        // Transform bars
//        Method method = MLBarDataLoader.class.getDeclaredMethod("getBarEntities", List.class);
//        method.setAccessible(true);
//        List<BarEntity> relativeBars = (List)method.invoke(null, bars);
//        assertEquals(bars.size() - 1, relativeBars.size());
//        
//        for(int i = 1; i < relativeBars.size(); i++){
//            Bar prev = bars.get(i-1);
//            Bar current = bars.get(i);
//            BarEntity relative = relativeBars.get(i-1);
//            assertEquals(current.getOpen()/prev.getOpen()-1.0, relative.getRelativeBar().getOpen(), 0.0001);
//            assertEquals(current.getHigh()/prev.getHigh()-1.0, relative.getRelativeBar().getHigh(), 0.0001);
//            assertEquals(current.getLow()/prev.getLow()-1.0, relative.getRelativeBar().getLow(), 0.0001);
//            assertEquals(current.getClose()/prev.getClose()-1.0, relative.getRelativeBar().getClose(), 0.0001);
//            assertEquals(current.getVolume()/prev.getVolume()-1.0, relative.getRelativeBar().getVolume(), 0.0001);
//        }
//    }
    
    @Test
    public void testGetOutputEntity() throws Exception{
        
        // Invoke private method using reflection
        Method method = MLBarDataLoader.class.getDeclaredMethod("getOutputEntity", List.class, Integer.TYPE, Calendar.class); //(List<Bar> bars, int pos, Calendar currentTime 
        method.setAccessible(true);
        IdealOutputEntity data = (IdealOutputEntity) method.invoke(null, barEntities, 0, barEntities.get(0).getTime());
        // Check not null
        assertNotNull( data);
        assertEquals(barEntities.get(1), data.getBarEntity());
        
    }
    
    /**
     * barsToMLData conversion test
     * @throws Exception 
     */
//    @Test
//    public void testBarsToMLData() throws Exception{
//        System.out.println("testBarsToMLData");
//        MLBarDataLoader dsl = new MLBarDataLoader();
//        // Invoke method
//        Method method = MLBarDataLoader.class.getDeclaredMethod("barsToMLData", List.class);
//        method.setAccessible(true);
//        MLData data = (MLData) method.invoke(dsl,bars);
//        
//        // Check the result
//        for(int i = 0; i < bars.size(); i++){
//            Bar bar = bars.get(i);
//            assertEquals(bar.getTime().getTimeInMillis(), data.getData(i*Bar.FIELD_COUNT),0.000000001);
//            assertEquals(bar.getOpen(), data.getData(i*Bar.FIELD_COUNT+1),0.000000001);
//            assertEquals(bar.getHigh(), data.getData(i*Bar.FIELD_COUNT+2),0.000000001);
//            assertEquals(bar.getLow(), data.getData(i*Bar.FIELD_COUNT+3),0.000000001);
//            assertEquals(bar.getClose(), data.getData(i*Bar.FIELD_COUNT+4),0.000000001);
//            assertEquals(bar.getVolume(), data.getData(i*Bar.FIELD_COUNT+5),0.000000001);
//        }
//    }
    
  /**
     * Get last bar before the time
     * @throws Exception 
     */
    @Test
    public void testGetLastPos() throws Exception{
        // Invoke method
        MLBarDataLoader dsl = new MLBarDataLoader();
        Method method = MLBarDataLoader.class.getDeclaredMethod("getLastPos", List.class, Calendar.class, Integer.TYPE);
        method.setAccessible(true); 
        int pos = (int)method.invoke(dsl,barEntities, barEntities.get(1).getTime(),1);
        assertEquals(pos,1);
        
    }    
}