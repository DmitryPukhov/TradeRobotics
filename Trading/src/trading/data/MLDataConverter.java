/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import trading.common.Config;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.InputData;
import trading.data.model.OutputData;
import trading.data.model.RelativeBar;

/**
 * Convert entities to MLData values
 * @author dima
 */
public class MLDataConverter {
    /**
     * Convert entity data pair to MLDataPair
     * @param pair
     * @return 
     */
    public static MLDataPair DataPairToMLDataPair(DataPair pair){
        // Get input and output
        MLData inputData = inputToMLData(pair.getInputData());
        MLData outputData = outputToMLData(pair.getOutputData());
        // Create data pair
        BasicMLDataPair mlDataPair = new BasicMLDataPair(inputData, outputData);
        return mlDataPair;
    }
    
    /**
     * Create MLData from output entity
     * @param output
     * @return 
     */
    public static MLData outputToMLData(OutputData output){
        double[] values = outputToArray(output);
        // Create ml data from values
        MLData result = new BasicMLData(values);
        return result;
    }
    
    /**
     * Create MLData from input entity
     * @param input
     * @return 
     */
    public static MLData inputToMLData(InputData input){
        MLData data = new BasicMLData(inputToArray(input));
        return data;
    }
    
    /**
     * Create data array from output entity
     * @param output
     * @return 
     */
    public static double[] outputToArray(OutputData output){
      // Fill values array
        double[] values = new double[Config.getOutputSize()];
        values[0] = output.getBar().getRelativeValue().getHigh();
        values[1] = output.getBar().getRelativeValue().getLow();
        return values;
    }
    
    /**
     * Constructs data array from bar list
     *
     * @param bars bar list like 3 bars: M1, M15, H1
     * @param prevBars bar list like 3 bars: M1, M15, H1
     * @return
     */
    public static double[] inputToArray(InputData input) {
        // Resulting array
        int arraySize = (input.getSmallBars().size() + input.getMediumBars().size() + input.getLargeBars().size()) * Bar.FIELD_COUNT;
        double[] result = new double[arraySize];
        
        int pos = 0;
        // insert small bars
        for (RelativeBar bar : input.getSmallBars()) {
            pos = insertBar(result, bar.getRelativeValue(), pos);
        }
        // insert medium bars
        for (RelativeBar bar : input.getMediumBars()) {
            pos = insertBar(result, bar.getRelativeValue(), pos);
        }
        // insert large bars
        for (RelativeBar bar : input.getLargeBars()) {
            pos = insertBar(result, bar.getRelativeValue(), pos);
        }
        return result;
    }
    /**
     * Insert bar to double[] array for MLData
     * @param array
     * @param bar 
     */
    private static int insertBar(double[] array, Bar bar, int pos){
            // Add bar to array. Use percentage instead of absolute values
            array[pos++] = (double) bar.getTime().getTimeInMillis();
            array[pos++] = bar.getOpen();
            array[pos++] = bar.getHigh();
            array[pos++] = bar.getLow();
            array[pos++] = bar.getClose();
            array[pos++] = bar.getVolume();
            return pos;
    }    
}
