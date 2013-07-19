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
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import trading.common.Config;

/**
 * Load bar data to dataset
 *
 * @author pdg
 */
public class MLDataSetLoader {

    private int smallPos;
    private int mediumPos;
    private int largePos;
    List<Bar> smallBars = new ArrayList<>();
    List<Bar> mediumBars = new ArrayList<>();
    List<Bar> largeBars = new ArrayList<>();

    /**
     * Create buffered ML data set from csv files for 3 bar periods Use file
     * name
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MLDataSet createBufferedMLDataSetFromFile() throws FileNotFoundException, IOException {
        MLDataSet ds;

            // Load from csv files to bar arrays
            BarFileLoader.load(Config.getSmallBarsFilePath(), smallBars);
            BarFileLoader.load(Config.getMediumBarsFilePath(), mediumBars);
            BarFileLoader.load(Config.getLargeBarsFilePath(), largeBars);
            
            // Transform bars to change percents
            transformBars(smallBars);
            transformBars(mediumBars);
            transformBars(largeBars);
            // Create dataset for machine learning
            ds = createBufferedMLDataSet();
            // Clear arrays to not store in memory
            smallBars.clear();
            mediumBars.clear();
            largeBars.clear();

        return ds;
    }
    
    /**
     * Transform bars from OHLC absolute values to change percent
     * @param bars 
     */
    private static void transformBars(List<Bar> bars){
        Iterator<Bar> currentIterator = bars.iterator();
        Iterator<Bar> prevIterator = bars.iterator();
        
        Bar prevBar = currentIterator.next();
        // Transform all bars except first (we don't know prev bar for the first one)
        while(currentIterator.hasNext()){
            Bar curBar = currentIterator.next();
            Bar originalCurBar = new Bar(curBar.getTime(), curBar.getOpen(), curBar.getHigh(), curBar.getLow(), curBar.getClose(), curBar.getVolume());

            // Set bar prices to change percents rather than absolute price values
            curBar.setOpen(curBar.getOpen()/prevBar.getOpen() - 1);
            curBar.setHigh(curBar.getHigh()/prevBar.getHigh() - 1);
            curBar.setLow(curBar.getLow()/prevBar.getLow() - 1);
            curBar.setClose(curBar.getClose()/prevBar.getClose() - 1);
            curBar.setVolume(curBar.getVolume()/prevBar.getVolume() - 1);
            
            prevBar = originalCurBar;
        }
        // Remove first bar because we can't calculate it's change percents
        bars.remove(0);
    }

    /**
     * Load to dataset
     */
    private MLDataSet createBufferedMLDataSet() throws FileNotFoundException, IOException {
        int inputSize = Config.getInputSize();

        // Get temp ml data file path
        String fileName = Config.getDataDir() + MLDataSetLoader.class.getName() + ".egb";
        File file = new File(fileName);

        // Create new dataset
        BufferedMLDataSet ds = new BufferedMLDataSet(file);
        ds.beginLoad(inputSize, Config.getOutputSize());

        mediumPos = mediumBars.size() - 1;
        largePos = largeBars.size() - 1;
        
        // Go through small pos
        for (smallPos = smallBars.size() - 1; smallPos > Config.getSmallBarsWindowSize(); smallPos--) {
            // Get window with last x small bars, last y medium bars, last z large bars
            List<Bar> window = getWindow(smallPos);


            // Create actual and result data for ML
            MLData inputData = barsToMLData(window);
            MLData outputData = getOutputData(mediumBars, mediumPos, smallBars.get(smallPos).getTime());

            // Add input/ideal pair to data set
            if (inputData != null && outputData != null) {
                ds.add(inputData, outputData);
            }
        }
        ds.endLoad();

        return ds;
    }

    /**
     * Returns window which contains small, medium, large bars before current
     * time This function changes local small, medium, large pos
     *
     * @param smallPos position of small bar
     * @return
     */
    private List<Bar> getWindow(int smallPos) {
        Bar smallBar = smallBars.get(smallPos);
        mediumPos = getLastPos(mediumBars, smallBar.getTime(), this.mediumPos); // Medium pos
        largePos = getLastPos(largeBars, smallBar.getTime(), this.largePos); // Large pos
        // Get data windows for small, medium, large bars
        List<Bar> smallWindow = getInputBars(smallBars, smallPos, Config.getSmallBarsWindowSize());
        List<Bar> mediumWindow = getInputBars(mediumBars, mediumPos, Config.getMediumBarsWindowSize());
        List<Bar> largeWindow = getInputBars(largeBars, largePos, Config.getLargeBarsWindowSize());

        // Create resulting window
        List<Bar> window = new ArrayList<>();
        window.addAll(smallWindow);
        window.addAll(mediumWindow);
        window.addAll(largeWindow);

        return window;
    }

    /**
     * Constructs data array from bar list
     *
     * @param bars bar list like 3 bars: M1, M15, H1
     * @param prevBars bar list like 3 bars: M1, M15, H1
     * @return
     */
    private static BasicMLData barsToMLData(List<Bar> bars) {
        if (bars.size() == 0) {
            return null;
        }
        double[] array = new double[bars.size() * Bar.FIELD_COUNT];
        int i = 0;
        for (Bar bar : bars) {
            // Add bar to array. Use percentage instead of absolute values
            array[i++] = (double) bar.getTime().getTimeInMillis();
            array[i++] = bar.getOpen();
            array[i++] = bar.getHigh();
            array[i++] = bar.getLow();
            array[i++] = bar.getClose();
            array[i++] = bar.getVolume();
        }
        BasicMLData data = new BasicMLData(array);
        return data;
    }

    /**
     * Get bars inside the window
     *
     * @param bars
     * @param endIndex
     * @param windowSize
     * @return
     */
    private static List<Bar> getInputBars(List<Bar> bars, int endIndex, int windowSize) {
        List<Bar> result = new ArrayList<>();
        if (endIndex - windowSize < 0) {
            return result;
        }
        result = bars.subList(endIndex-windowSize, endIndex);
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
    private static MLData getOutputData(List<Bar> bars, int pos, Calendar currentTime) {
        if (pos >= bars.size() - 1) {
            return null;
        }
        MLData result = null;
        Bar inputBar = bars.get(pos);
        long currentMillis = currentTime.getTimeInMillis();
        long predictionIntervalMillis = Config.getPredictionIntervalMillis();
        // Bar with result data
        Bar outputBar = null;
        for (int i = pos; i < bars.size(); i++) {
            Bar bar = bars.get(i);
            long intervalMillis = bar.getTime().getTimeInMillis() - currentMillis;

            // If bar after time interval
            if (intervalMillis >= predictionIntervalMillis) {
                outputBar = bar;
                break;
            }
        }
        // Get ML data from next bar
        if (outputBar != null) {
            // Get change percent from already transformed bar and put into output data
            double[] data = new double[]{outputBar.getHigh(), outputBar.getLow()};
            result = new BasicMLData(data);
        }
        // Null if no bars after interval
        return result;
    }

    /**
     * Get first pos after time
     *
     * @param initialPos
     * @param time
     */
    private static int getNearestPos(List<Bar> bars, Calendar time, int initialPos) {
        // No nearest pos if bars are empty
        if (bars.isEmpty()) {
            return -1;
        }

        // Calculate position of nearest bar
        int pos = -1;
        for (int i = initialPos; i < bars.size(); i++) {
            Bar bar = bars.get(i);

            // We found the first bar closed after the time
            // The next will open after the time
            if (bar.getTime().getTimeInMillis() >= time.getTimeInMillis()) {
                // Next bar of -1 if current bar is the last
                pos = (i < bars.size() - 1) ? ++i : -1;
                break;
            }
        }

        return pos;
    }

    /**
     * Get last position of a bar before the time
     *
     * @param bars
     * @param time
     * @param initialPos
     * @return
     */
    private static int getLastPos(List<Bar> bars, Calendar time, int initialPos) {
        if (bars.isEmpty()) {
            return -1;
        }
        int pos = -1;
        for (int i = initialPos; i >= 0; i--) {
            Bar bar = bars.get(i);
            // We found the bar closed before current time
            if (bar.getTime().getTimeInMillis() <= time.getTimeInMillis()) {
                pos = i;
                break;
            }
        }

        return pos;
    }
}
