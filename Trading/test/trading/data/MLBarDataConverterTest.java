/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.util.ArrayList;
import java.util.List;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.BarEntity;
import trading.data.model.OutputEntity;
import trading.data.model.InputEntity;

/**
 *
 * @author pdg
 */
public class MLBarDataConverterTest {

    public MLBarDataConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
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

    /**
     * Test of barsToEntities method, of class MLBarDataConverter.
     */
    @Test
    public void testBarsToEntities() {
        List<BarEntity> entities = MLBarDataConverter.barsToEntities(bars);
        assertEquals(bars.size() - 1, entities.size());
        for (int i = 0; i < entities.size(); i++) {
            Bar prevBar = bars.get(i); // Bars has a shift from entities
            Bar bar = bars.get(i + 1);
            BarEntity entity = entities.get(i);
            // Assert bar entity
            assertEquals(prevBar, entity.getPreviousAbsoluteBar());
            assertEquals(bar, entity.getAbsoluteBar());
            assertEquals(entity.getRelativeBar().getOpen(), (bar.getOpen() - prevBar.getOpen()) / prevBar.getOpen(), 0.00000000001);
            assertEquals(entity.getRelativeBar().getHigh(), (bar.getHigh() - prevBar.getHigh()) / prevBar.getHigh(), 0.00000000001);
            assertEquals(entity.getRelativeBar().getLow(), (bar.getLow() - prevBar.getLow()) / prevBar.getLow(), 0.00000000001);
            assertEquals(entity.getRelativeBar().getClose(), (bar.getClose() - prevBar.getClose()) / prevBar.getClose(), 0.00000000001);
        }
    }

    /**
     * Test of entityPairToMLDataPair method, of class MLBarDataConverter.
     */
//    @Test
//    public void testEntityPairToMLDataPair() {
//        InputEntity input = new InputEntity(barEntities, barEntities, barEntities);
//        IdealOutputEntity output = new IdealOutputEntity(barEntities.get(0));
//        DataPair pair = new DataPair(input, output);
//        assertEquals(pair.getInputEntity(), input);
//        assertEquals(pair.getOutputEntity(), output);
//    }

    /**
     * Test of outputEntityToMLData method, of class MLBarDataConverter.
     */
//    @Test
//    public void testOutputEntityToMLData() {
//        System.out.println("outputEntityToMLData");
//        IdealOutputEntity outputEntity = new IdealOutputEntity(barEntities.get(0));
//        MLData data = MLBarDataConverter.outputEntityToMLData(outputEntity);
//        assertNotNull(data);
//        // MLData should contain high and low price
//        assertEquals(outputEntity.getRelativeHigh(), data.getData(0), 0.000000001);
//        assertEquals(outputEntity.getRelativeLow(), data.getData(1), 0.000000001);        
//    }

    /**
     * Test of inputEntityToMLData method, of class MLBarDataConverter.
     */
    @Test
    public void testInputEntityToMLData() {
        System.out.println("inputEntityToMLData");
        InputEntity input = new InputEntity(barEntities, barEntities, barEntities);
        MLData result = MLBarDataConverter.inputEntityToMLData(input);
        
        assertInputEntityArray(input, result.getData());


    }

    /**
     * Test of outputEntityToArray method, of class MLBarDataConverter.
     */
//    @Test
//    public void testOutputEntityToArray() {
//        System.out.println("outputEntityToArray");
//        IdealOutputEntity output = new IdealOutputEntity(barEntities.get(0));
//        double[] result = MLBarDataConverter.outputEntityToArray(output);
//        assertEquals(output.getRelativeHigh(), result[0], 0.000000001);
//       assertEquals(output.getRelativeLow(), result[1], 0.000000001);        
//
//    }

    /**
     * Test of inputEntityToArray method, of class MLBarDataConverter.
     */
    @Test
    public void testInputEntityToArray() {
        System.out.println("inputEntityToArray");
        InputEntity input = new InputEntity(barEntities, barEntities, barEntities);
        double[] result = MLBarDataConverter.inputEntityToArray(input);
        // Assert data
        assertInputEntityArray(input, result);

    }
    /**
     * Assert result array for input entity
     * @param entity
     * @param result 
     */
    private void assertInputEntityArray(InputEntity entity, double[] result){
        List<BarEntity> bars = entity.getSmallBars();
        for(int i = 0; i < bars.size(); i++){
            assertEquals("open price", bars.get(i).getRelativeBar().getOpen(), result[i*Bar.FIELD_COUNT +0], 0.000000001);
            assertEquals("high price", bars.get(i).getRelativeBar().getHigh(), result[i*Bar.FIELD_COUNT +1], 0.000000001);
            assertEquals("low price", bars.get(i).getRelativeBar().getLow(), result[i*Bar.FIELD_COUNT +2], 0.000000001);
            assertEquals("close price", bars.get(i).getRelativeBar().getClose(), result[i*Bar.FIELD_COUNT +3], 0.000000001);
            assertEquals("volume", bars.get(i).getRelativeBar().getVolume(), result[i*Bar.FIELD_COUNT +4], 0.000000001);           
        }        
    }
}