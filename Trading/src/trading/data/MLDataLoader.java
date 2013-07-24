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
import trading.common.Config;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.InputData;
import trading.data.model.OutputData;
import trading.data.model.RelativeBar;

/**
 * Load bar data to dataset
 *
 * @author pdg
 */
public class MLDataLoader {

//    private int smallPos;
//    private int mediumPos;
//    private int largePos;
//    List<Bar> smallBars = new ArrayList<>();
//    List<Bar> mediumBars = new ArrayList<>();
//    List<Bar> largeBars = new ArrayList<>();
    /**
     * Create buffered ML data set from csv files for 3 bar periods Use file
     * name
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MLDataSet getMLDataSet() throws FileNotFoundException, IOException {
        MLDataSet ds = null;
        // Load from csv files to bar arrays
        List<Bar> smallSimpleBars = BarFileLoader.load(Config.getSmallBarsFilePath());
        List<Bar> mediumSimpleBars = BarFileLoader.load(Config.getMediumBarsFilePath());
        List<Bar> largeSimpleBars = BarFileLoader.load(Config.getLargeBarsFilePath());

        // Transform bars to relative bars
        List<RelativeBar> smallBars = getRelativeBars(smallSimpleBars);
        List<RelativeBar> mediumBars = getRelativeBars(mediumSimpleBars);
        List<RelativeBar> largeBars = getRelativeBars(largeSimpleBars);
        
        // Bars dates validation
        validateBars(smallBars, mediumBars, largeBars);

        // Transform bars to change percents
        List<DataPair> dataPairs = getDataPairs(smallBars, mediumBars, largeBars);
        
        // Create dataset for machine learning
        ds = getMLDataSet(dataPairs);
        return ds;
    }

    /**
     * Create BufferedMLDataSet from data entities
     * @param pairs 
     */
    private MLDataSet getMLDataSet(List<DataPair> pairs){
       // Create buffered ml data set
        String fileName = Config.getDataDir() + MLDataLoader.class.getName() + ".egb";
        File file = new File(fileName);
        BufferedMLDataSet ds = new BufferedMLDataSet(file);
        
        // Add data pairs
        ds.beginLoad(Config.getInputSize(), Config.getOutputSize());
        for(DataPair pair: pairs){
           MLDataPair mlDataPair = MLDataConverter.DataPairToMLDataPair(pair);
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
    private List<DataPair> getDataPairs(List<RelativeBar> smallBars, List<RelativeBar> mediumBars, List<RelativeBar> largeBars){
        List<DataPair> pairs = new ArrayList<>();

        int smallPos, mediumPos, largePos;
        mediumPos = mediumBars.size() - 1;
        largePos = largeBars.size() - 1;

        // Go through small pos
        for (smallPos = smallBars.size() - 1; smallPos > Config.getSmallBarsWindowSize(); smallPos--) {
            // Get window with last x small bars, last y medium bars, last z large bars
            RelativeBar smallBar = smallBars.get(smallPos);
            mediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumPos); // Medium pos
            largePos = getLastPos(largeBars, smallBar.getTime(), largePos); // Large pos     
            
            InputData input = getInputData(smallBars, smallPos, mediumBars, mediumPos, largeBars, largePos);
            OutputData ideal = getOutputData(mediumBars, mediumPos, smallBar.getTime());

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
    private void validateBars(List<RelativeBar> smallBars, List<RelativeBar> mediumBars, List<RelativeBar> largeBars) {

        for (RelativeBar smallBar : smallBars) {
            // Medium bars validation
            int lastMediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumBars.size() - 1);
            if (lastMediumPos == -1) {
                throw new Error(String.format("Can't find medium bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastMediumPos - Config.getMediumBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find medium bars window with size: %d for date %s", Config.getMediumBarsWindowSize(), smallBar.getTime().getTime().toString()));
            }

            // Large bars validation
            int lastLargePos = getLastPos(largeBars, smallBar.getTime(), largeBars.size() - 1);
            if (lastLargePos == -1) {
                throw new Error(String.format("Can't find large bar for date %s", smallBar.getTime().getTime().toString()));
            }
            if (lastLargePos - Config.getLargeBarsWindowSize() < 0) {
                throw new Error(String.format("Can't find large bars window with size: %d for date %s", Config.getLargeBarsWindowSize(), smallBar.getTime().getTime().toString()));
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
    private InputData getInputData(List<RelativeBar> smallBars, int smallPos, List<RelativeBar> mediumBars, int mediumPos, List<RelativeBar> largeBars, int largePos) {
        RelativeBar smallBar = smallBars.get(smallPos);
        mediumPos = getLastPos(mediumBars, smallBar.getTime(), mediumPos); // Medium pos
        largePos = getLastPos(largeBars, smallBar.getTime(), largePos); // Large pos
        // Get data windows for small, medium, large bars
        List<RelativeBar> smallWindow = getInputWindow(smallBars, smallPos, Config.getSmallBarsWindowSize());
        List<RelativeBar> mediumWindow = getInputWindow(mediumBars, mediumPos, Config.getMediumBarsWindowSize());
        List<RelativeBar> largeWindow = getInputWindow(largeBars, largePos, Config.getLargeBarsWindowSize());

        InputData input = new InputData(smallWindow, mediumWindow, largeWindow);

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
    private static OutputData getOutputData(List<RelativeBar> bars, int pos, Calendar currentTime) {
        if (pos >= bars.size() - 1) {
            return null;
        }
        OutputData result = null;
        RelativeBar inputBar = bars.get(pos);
        long currentMillis = currentTime.getTimeInMillis();
        long predictionIntervalMillis = Config.getPredictionIntervalMillis();
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
            result = new OutputData(outputBar);
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
