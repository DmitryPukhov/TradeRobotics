/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import trading.common.NeuralContext;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.InputEntity;
import trading.data.model.IdealOutputEntity;
import trading.data.model.BarEntity;

/**
 * Convert entities to MLData values
 * @author dima
 */
public class MLBarDataConverter {
    
    /**
     * Transform bars from OHLC absolute values to change percent
     *
     * @param bars
     */
    public static List<BarEntity> barsToEntities(List<Bar> bars) {
        List<BarEntity> relativeBars = new ArrayList<>();
        
        Iterator<Bar> currentIterator = bars.iterator();
        Iterator<Bar> prevIterator = bars.iterator();

        Bar prevBar = currentIterator.next();
        // Transform all bars except first (we don't know prev bar for the first one)
        while (currentIterator.hasNext()) {
            Bar curBar = currentIterator.next();
            // Create relative bar from current and previous
            BarEntity relativeBar = new BarEntity(curBar, prevBar);
            relativeBars.add(relativeBar);
            // Current bar becomes previous
            prevBar = curBar;
        }
        return relativeBars;
    }    
    /**
     * Convert entity data pair to MLDataPair
     * @param pair
     * @return 
     */
    public static MLDataPair entityPairToMLDataPair(DataPair pair){
        // Get input and output
        MLData inputData = inputEntityToMLData(pair.getInputEntity());
        MLData outputData = outputEntityToMLData(pair.getOutputEntity());
        // Create data pair
        BasicMLDataPair mlDataPair = new BasicMLDataPair(inputData, outputData);
        return mlDataPair;
    }
    
    /**
     * Create MLData from output entity
     * @param output
     * @return 
     */
    public static MLData outputEntityToMLData(IdealOutputEntity output){
        double[] values = outputEntityToArray(output);
        // Create ml data from values
        MLData result = new BasicMLData(values);
        return result;
    }
    
    /**
     * Create MLData from input entity
     * @param input
     * @return 
     */
    public static MLData inputEntityToMLData(InputEntity input){
        MLData data = new BasicMLData(inputEntityToArray(input));
        return data;
    }
    
    /**
     * Create data array from output entity
     * @param output
     * @return 
     */
    public static double[] outputEntityToArray(IdealOutputEntity output){
      // Fill values array
        double[] values = new double[NeuralContext.NetworkSettings.getOutputSize()];
        values[0] = output.getBarEntity().getRelativeBar().getHigh();
        values[1] = output.getBarEntity().getRelativeBar().getLow();
        return values;
    }
    
    /**
     * Constructs data array from bar list
     *
     * @param bars bar list like 3 bars: M1, M15, H1
     * @param prevBars bar list like 3 bars: M1, M15, H1
     * @return
     */
    public static double[] inputEntityToArray(InputEntity input) {
        // Resulting array
        int arraySize = (input.getSmallBars().size() + input.getMediumBars().size() + input.getLargeBars().size()) * Bar.FIELD_COUNT;
        double[] result = new double[arraySize];
        
        int pos = 0;
        // insert small bars
        for (BarEntity bar : input.getSmallBars()) {
            pos = insertRelativeBar(result, bar, pos);
        }
        // insert medium bars
        for (BarEntity bar : input.getMediumBars()) {
            pos = insertRelativeBar(result, bar, pos);
        }
        // insert large bars
        for (BarEntity bar : input.getLargeBars()) {
            pos = insertRelativeBar(result, bar, pos);
        }
        return result;
    }
    /**
     * Insert bar to double[] array for MLData
     * @param array
     * @param bar 
     */
    private static int insertRelativeBar(double[] array, BarEntity entity, int pos){
            // Add bar to array. Use percentage instead of absolute values
            //array[pos++] = entity.getRelativeBar().getTime().getTimeInMillis()/entity.getPreviousAbsoluteBar().getTime().getTimeInMillis();
//            Calendar cal = entity.getRelativeBar().getTime();
//            array[pos++] =  cal.get(Calendar.DAY_OF_MONTH)/cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//            array[pos++] = cal.get(Calendar.DAY_OF_WEEK)/cal.getActualMaximum(Calendar.DAY_OF_WEEK);
//            array[pos++] = cal.get(Calendar.HOUR)/cal.getActualMaximum(Calendar.HOUR);
//            array[pos++] = cal.get(Calendar.MINUTE)/cal.getActualMaximum(Calendar.MINUTE);
        
            array[pos++] = entity.getRelativeBar().getOpen();
            array[pos++] = entity.getRelativeBar().getHigh();
            array[pos++] = entity.getRelativeBar().getLow();
            array[pos++] = entity.getRelativeBar().getClose();
            array[pos++] = entity.getRelativeBar().getVolume();//bar.getVolume();
            return pos;
    }    
}
