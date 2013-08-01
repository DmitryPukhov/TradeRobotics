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
import trading.common.NeuralContext;
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
        smallBars = TestUtilities.createLinearBars();
        barEntities = MLBarDataConverter.barsToEntities(smallBars);
    }
    

            
    
    @After
    public void tearDown() {
    }
    
    //</editor-fold>
    
    /**
     * Bars for loading to dataset
     */
    private List<Bar> smallBars = new ArrayList<>();
    private List<BarEntity> barEntities = new ArrayList<>();

    
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