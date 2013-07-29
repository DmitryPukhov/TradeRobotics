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
import java.util.Iterator;
import java.util.List;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import trading.common.NeuralContext;
import trading.data.model.Bar;
import trading.data.model.EntityPair;
import trading.data.model.InputEntity;
import trading.data.model.OutputEntity;
import trading.data.model.RelativeBar;

/**
 * Load bar data to dataset
 *
 * @author pdg
 */
public class MLDataLoader {

    /**
     * Get entity pairs from csv file
     * @return 
     */
    public static List<EntityPair> getEntityPairs(String smallBarsFilePath, String mediumBarsFilepath, String largeBarsFilePath) throws FileNotFoundException, IOException{
        // Load from csv files to bar arrays
        List<Bar> smallSimpleBars = BarFileLoader.load(NeuralContext.Files.getSmallBarsFilePath());
        List<Bar> mediumSimpleBars = BarFileLoader.load(NeuralContext.Files.getMediumBarsFilePath());
        List<Bar> largeSimpleBars = BarFileLoader.load(NeuralContext.Files.getLargeBarsFilePath());

        // Transform bars to relative bars
        List<RelativeBar> smallBars = getRelativeBars(smallSimpleBars);
        List<RelativeBar> mediumBars = getRelativeBars(mediumSimpleBars);
        List<RelativeBar> largeBars = getRelativeBars(largeSimpleBars);
        
        // Bars dates validation
        validateBars(smallBars, mediumBars, largeBars);

        // Transform bars to change percents
        List<EntityPair> pairs = getEntityPairs(smallBars, mediumBars, largeBars);
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
    public static MLDataSet getMLDataSet() throws FileNotFoundException, IOException {


        // Transform bars to change percents
        List<EntityPair> dataPairs = getEntityPairs(NeuralContext.Files.getSmallBarsFilePath(), NeuralContext.Files.getMediumBarsFilePath(), NeuralContext.Files.getLargeBarsFilePath());
        
        // Create dataset for machine learning
        MLDataSet ds = getMLDataSet(dataPairs);
        return ds;
    }

    /**
     * Create BufferedMLDataSet from data entities
     * @param pairs 
     */
    private static MLDataSet getMLDataSet(List<EntityPair> pairs){
       // Create buffered ml data set
        String fileName = NeuralContext.Files.getDataDir() + MLDataLoader.class.getName() + ".egb";
        File file = new File(fileName);
        BufferedMLDataSet ds = new BufferedMLDataSet(file);
        
        // Add data pairs
        ds.beginLoad(NeuralContext.NetworkSettings.getInputSize(), NeuralContext.NetworkSettings.getOutputSize());
        for(EntityPair pair: pairs){
           MLDataPair mlDataPair = MLDataConverter.EntityPairToMLDataPair(pair);
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
    public static List<EntityPair> getEntityPairs(List<RelativeBar> smallBars, List<RelativeBar> mediumBars, List<RelativeBar> largeBars){
        List<EntityPair> pairs = new ArrayList<>();

        int smallPos, mediumPos, largePos;
        mediumPos = mediumBars.size() - 1;
        largePos = largeBars.size() - 1;

        // Go through small pos
        for (smallPos = smallBars.size() - 1; smallPos > NeuralContext.NetworkSettings.getSmallBarsWindowSize(); smallPos--) {
            // Get window with last x small bars, last y medium bars, last z large bars
            RelativeBar smallBar = smallBars.get(smallPos);
            mediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumPos); // Medium pos
            largePos = getLastPos(largeBars, smallBar.getTime(), largePos); // Large pos     
            
            InputEntity input = getInputEntity(smallBars, smallPos, mediumBars, mediumPos, largeBars, largePos);
            OutputEntity ideal = getOutputEntity(mediumBars, mediumPos, smallBar.getTime());

            // Create input/ideal pair
            if (input != null && ideal != null) {
                EntityPair pair = new EntityPair(input, ideal);
                pairs.add(pair);
            }
        }
        
        return pairs;
    }

    /**
     * Validate if medium and large bars matches
     */
    private static void validateBars(List<RelativeBar> smallBars, List<RelativeBar> mediumBars, List<RelativeBar> largeBars) {

        for (RelativeBar smallBar : smallBars) {
            // Medium bars validation
            int lastMediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumBars.size() - 1);
            if (lastMediumPos == -1) {
                throw new Error(String.format("Can't find medium bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastMediumPos - NeuralContext.NetworkSettings.getMediumBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find medium bars window with size: %d for date %s", NeuralContext.NetworkSettings.getMediumBarsWindowSize(), smallBar.getTime().getTime().toString()));
            }

            // Large bars validation
            int lastLargePos = getLastPos(largeBars, smallBar.getTime(), largeBars.size() - 1);
            if (lastLargePos == -1) {
                throw new Error(String.format("Can't find large bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastLargePos - NeuralContext.NetworkSettings.getLargeBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find large bars window with size: %d for date %s", NeuralContext.NetworkSettings.getLargeBarsWindowSize(), smallBar.getTime().getTime().toString()));
            }
        }
    }

    /**
     * Transform bars from OHLC absolute values to change percent
     *
     * @param bars
     */
    private static List<RelativeBar> getRelativeBars(List<Bar> bars) {
        List<RelativeBar> relativeBars = new ArrayList<>();
        
        Iterator<Bar> currentIterator = bars.iterator();
        Iterator<Bar> prevIterator = bars.iterator();

        Bar prevBar = currentIterator.next();
        // Transform all bars except first (we don't know prev bar for the first one)
        while (currentIterator.hasNext()) {
            Bar curBar = currentIterator.next();
            // Create relative bar from current and previous
            RelativeBar relativeBar = new RelativeBar(curBar, prevBar);
            relativeBars.add(relativeBar);
            // Current bar becomes previous
            prevBar = curBar;
        }
        return relativeBars;
    }

    /**
     * Returns window which contains small, medium, large bars before current
     * time This function changes local small, medium, large pos
     *
     * @param smallPos position of small bar
     * @return
     */
    private static InputEntity getInputEntity(List<RelativeBar> smallBars, int smallPos, List<RelativeBar> mediumBars, int mediumPos, List<RelativeBar> largeBars, int largePos) {
        RelativeBar smallBar = smallBars.get(smallPos);
        mediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumPos); // Medium pos
        largePos = getLastPos(largeBars, smallBar.getTime(), largePos); // Large pos
        // Get data windows for small, medium, large bars
        List<RelativeBar> smallWindow = getInputWindow(smallBars, smallPos, NeuralContext.NetworkSettings.getSmallBarsWindowSize());
        List<RelativeBar> mediumWindow = getInputWindow(mediumBars, mediumPos, NeuralContext.NetworkSettings.getMediumBarsWindowSize());
        List<RelativeBar> largeWindow = getInputWindow(largeBars, largePos, NeuralContext.NetworkSettings.getLargeBarsWindowSize());

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
    private static List<RelativeBar> getInputWindow(List<RelativeBar> bars, int endIndex, int windowSize) {
        List<RelativeBar> result = new ArrayList<>();
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
    private static OutputEntity getOutputEntity(List<RelativeBar> bars, int pos, Calendar currentTime) {
        if (pos >= bars.size() - 1) {
            return null;
        }
        OutputEntity result = null;
        RelativeBar inputBar = bars.get(pos);
        long currentMillis = currentTime.getTimeInMillis();
        long predictionIntervalMillis = NeuralContext.NetworkSettings.getPredictionIntervalMillis();
        // Bar with result data
        RelativeBar outputBar = null;
        for (int i = pos; i < bars.size(); i++) {
            RelativeBar bar = bars.get(i);
            long intervalMillis = bar.getTime().getTimeInMillis() - currentMillis;

            // If bar after time interval
            if (intervalMillis >= predictionIntervalMillis) {
                outputBar = bar;
                break;
            }
        }
        // Get ML data from next bar
        if (outputBar != null) {
            result = new OutputEntity(outputBar);
        }
        // Null if no bars after interval
        return result;
    }

    /**
     * Get last position of a bar before the time
     *
     * @param bars
     * @param time
     * @param initialPos
     * @return
     */
    private static int getLastPos(List<RelativeBar> bars, Calendar time, int initialPos) {
        if (bars.isEmpty()) {
            return -1;
        }
        int pos = -1;
        for (int i = initialPos; i >= 0; i--) {
            RelativeBar bar = bars.get(i);
            // We found the bar closed before current time
            if (bar.getTime().getTimeInMillis() <= time.getTimeInMillis()) {
                pos = i;
                break;
            }
        }

        return pos;
    }
}
