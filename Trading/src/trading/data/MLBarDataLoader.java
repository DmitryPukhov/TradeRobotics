/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import trading.common.NeuralContext;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.InputEntity;
import trading.data.model.OutputEntity;
import trading.data.model.BarEntity;

/**
 * Load bar data to dataset
 *
 * @author pdg
 */
public class MLBarDataLoader {
    /**
     * Gets data pars with train data
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static List<DataPair> getTrainEntityPairs() throws FileNotFoundException, IOException{
        //return getTestEntityPairs();
        return getEntityPairs(NeuralContext.Files.getSmallBarsTrainFilePath(), NeuralContext.Files.getMediumBarsTrainFilePath(), NeuralContext.Files.getLargeBarsTrainFilePath());
    }
 
    /**
     * Gets data pars with test data
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static List<DataPair> getTestEntityPairs() throws FileNotFoundException, IOException{
        return getEntityPairs(NeuralContext.Files.getSmallBarsTestFilePath(), NeuralContext.Files.getMediumBarsTestFilePath(), NeuralContext.Files.getLargeBarsTestFilePath());
    }    
    
    /**
     * Get entity pairs from csv file
     * @return 
     */
    public static List<DataPair> getEntityPairs(String smallBarsFilePath, String mediumBarsFilePath, String largeBarsFilePath) throws FileNotFoundException, IOException{
        // Load from csv files to bar arrays
        List<Bar> smallSimpleBars = BarFileLoader.load(smallBarsFilePath);
        List<Bar> mediumSimpleBars = BarFileLoader.load(mediumBarsFilePath);
        List<Bar> largeSimpleBars = BarFileLoader.load(largeBarsFilePath);
        
        // Transform bars to relative bars
        List<BarEntity> smallBars = MLBarDataConverter.barsToEntities(smallSimpleBars);
        List<BarEntity> mediumBars = MLBarDataConverter.barsToEntities(mediumSimpleBars);
        List<BarEntity> largeBars = MLBarDataConverter.barsToEntities(largeSimpleBars);
        
        // Bars dates validation
        validateBars(smallBars, mediumBars, largeBars);

        // Transform bars to change percents
        List<DataPair> pairs = getEntityPairs(smallBars, mediumBars, largeBars);
        return pairs;
        
    }
    
   
    /**
     * Create buffered ML data set from csv files for 3 bar periods Use file
     * name
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static MLDataSet getTrainMLDataSet() throws FileNotFoundException, IOException {

        // Transform bars to change percents
        List<DataPair> dataPairs = getTrainEntityPairs();
        
        int firstIndex = Math.max((dataPairs.size()-1 - NeuralContext.Training.getSamplesCount()), 0);
        int lastIndex = Math.max(dataPairs.size()-1, 0);
        List<DataPair> samplesPairs = dataPairs.subList(firstIndex, lastIndex);
        // Create dataset for machine learning
        MLDataSet ds = getMLDataSet(dataPairs);
        return ds;
    }

    /**
     * Create BufferedMLDataSet from data entities
     * @param pairs 
     */
    private static MLDataSet getMLDataSet(List<DataPair> pairs){
       // Create buffered ml data set
        String fileName = NeuralContext.Files.getDataDir() + MLBarDataLoader.class.getName() + ".egb";
        File file = new File(fileName);
        BufferedMLDataSet ds = new BufferedMLDataSet(file);
        
        // Add data pairs
        ds.beginLoad(NeuralContext.NetworkSettings.getInputSize(), NeuralContext.NetworkSettings.getOutputSize());
        for(DataPair pair: pairs){
           MLDataPair mlDataPair = MLBarDataConverter.entityPairToMLDataPair(pair);
           ds.add(mlDataPair);
        }
        ds.endLoad();
        
        return ds;
    }
    
    /**
     * Get network data pairs from bar lists
     * @param smallBars
     * @param mediumBars
     * @param largeBars
     * @return 
     */
    public static List<DataPair> getEntityPairs(List<BarEntity> smallBars, List<BarEntity> mediumBars, List<BarEntity> largeBars){
        List<DataPair> pairs = new ArrayList<>();

        // Start from prediction interval from the end (for prediction test)
        int smallPos;
        int predictionInterval = (int)NeuralContext.NetworkSettings.getPredictionIntervalMillis();
        Calendar lastSmallTime = GregorianCalendar.getInstance();
        lastSmallTime.setTimeInMillis(smallBars.get(smallBars.size()-1).getTime().getTimeInMillis());
        lastSmallTime.add(Calendar.MILLISECOND, -predictionInterval);
        int lastSmallPos = getLastPosNotLater(smallBars,lastSmallTime);
 

        // Go through small pos
        for (smallPos = NeuralContext.NetworkSettings.getSmallBarsWindowSize(); smallPos <= lastSmallPos; smallPos++) {
            // Get window with last x small bars, last y medium bars, last z large bars
            
            InputEntity input = getInputEntity(smallBars, smallPos, mediumBars, largeBars);
            OutputEntity ideal = getOutputEntity(smallBars, smallPos);

            // Create input/ideal pair
            if (input != null && ideal != null) {
                DataPair pair = new DataPair(input, ideal);
                pairs.add(pair);
            }
        }
        
        return pairs;
    }

    /**
     * Validate if medium and large bars matches
     */
    private static void validateBars(List<BarEntity> smallBars, List<BarEntity> mediumBars, List<BarEntity> largeBars) {

        for (BarEntity smallBar : smallBars) {
            // Medium bars validation
            int lastMediumPos = getLastPosNotLater(mediumBars, smallBar.getTime());
            if (lastMediumPos == -1) {
                throw new Error(String.format("Can't find medium bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastMediumPos - NeuralContext.NetworkSettings.getMediumBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find medium bars window with size: %d for date %s", NeuralContext.NetworkSettings.getMediumBarsWindowSize(), smallBar.getTime().getTime().toString()));
            }

            // Large bars validation
            int lastLargePos = getLastPosNotLater(largeBars, smallBar.getTime());
            if (lastLargePos == -1) {
                throw new Error(String.format("Can't find large bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastLargePos - NeuralContext.NetworkSettings.getLargeBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find large bars window with size: %d for date %s", NeuralContext.NetworkSettings.getLargeBarsWindowSize(), smallBar.getTime().getTime().toString()));
            }
        }
    }



    /**
     * Returns window which contains small, medium, large bars before current
     * time This function changes local small, medium, large pos
     *
     * @param smallPos position of small bar
     * @return
     */
    private static InputEntity getInputEntity(List<BarEntity> smallBars, int smallPos, List<BarEntity> mediumBars, List<BarEntity> largeBars) {
        BarEntity smallBar = smallBars.get(smallPos);
        int mediumPos = getLastPosNotLater(mediumBars, smallBar.getTime()); // Medium pos
        int largePos = getLastPosNotLater(largeBars, smallBar.getTime()); // Large pos
        // Get data windows for small, medium, large bars
        List<BarEntity> smallWindow = getInputWindow(smallBars, smallPos, NeuralContext.NetworkSettings.getSmallBarsWindowSize());
        List<BarEntity> mediumWindow = getInputWindow(mediumBars, mediumPos, NeuralContext.NetworkSettings.getMediumBarsWindowSize());
        List<BarEntity> largeWindow = getInputWindow(largeBars, largePos, NeuralContext.NetworkSettings.getLargeBarsWindowSize());

        InputEntity input = new InputEntity(smallWindow, mediumWindow, largeWindow);

        return input;
    }


    
    
    /**
     * Get bars inside the window
     *
     * @param bars
     * @param endIndex
     * @param windowSize
     * @return
     */
    private static List<BarEntity> getInputWindow(List<BarEntity> bars, int endIndex, int windowSize) {
        List<BarEntity> result = new ArrayList<>();
        if (endIndex - windowSize < 0) {
            return result;
        }
        result = bars.subList(endIndex - windowSize, endIndex);
        return result;
    }

    /**
     * Get data for decision result
     *
     * @param bars
     * @param pos
     * @param currentTime
     * @return
     */
    private static OutputEntity getOutputEntity(List<BarEntity> bars, int index) {
        int startIndex = index;
        if (startIndex == -1) {
            return null;
        }
        OutputEntity result = null;
        BarEntity bar = bars.get(index);
        long currentMillis = bar.getTime().getTimeInMillis();
        long predictionIntervalMillis = NeuralContext.NetworkSettings.getPredictionIntervalMillis();
        double highBoundAbsolute = 0;
        double lowBoundAbsolute = Double.MAX_VALUE;
        
        // Get high and low boundds within interval
        for (int i = startIndex; i < bars.size(); i++) {
            BarEntity currentBar = bars.get(i);
            long intervalMillis = currentBar.getTime().getTimeInMillis() - currentMillis;

            // If bar after time interval
            if (intervalMillis >= predictionIntervalMillis) {
                //outputBar = bar;
                break;
            }
            highBoundAbsolute = Math.max(highBoundAbsolute, currentBar.getAbsoluteBar().getHigh());
            lowBoundAbsolute = Math.min(lowBoundAbsolute, currentBar.getAbsoluteBar().getLow());
        }
        result = OutputEntity.createFromAbsoluteData(bar, predictionIntervalMillis,  highBoundAbsolute, lowBoundAbsolute);
        // Null if no bars after interval
        return result;
    }

    
    
    /**
     * Get last position of a bar before or at the time
     *
     * @param bars
     * @param time
     * @param initialPos
     * @return
     */
    private static int getLastPosNotLater(List<BarEntity> bars, Calendar time) {
        if (bars.isEmpty()) {
            return -1;
        }
        int pos = -1;
        BarEntity prevBar = null;
        // Find first later bar
        for(BarEntity bar: bars){
            // If current bar is later, return previous one which was before the time
             if (bar.getTime().getTimeInMillis() > time.getTimeInMillis()) {
                pos = bars.indexOf(prevBar);
                break;
            }  
            prevBar = bar;
        }
        return pos;
    }
     
}
