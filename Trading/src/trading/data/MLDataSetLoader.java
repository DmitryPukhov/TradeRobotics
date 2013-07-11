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
import java.util.List;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import trading.common.Config;

/**
 * Load bar data to dataset
 * @author pdg
 */
public class MLDataSetLoader {

    /**
     * Create buffered ML data set from csv files for 3 bar periods
     * Use file name
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static MLDataSet createBufferedMLDataSet() throws FileNotFoundException, IOException{
        MLDataSet ds;
        ArrayList<Bar> smallBars = new ArrayList<>();
        ArrayList<Bar> mediumBars = new ArrayList<>();
        ArrayList<Bar> largeBars = new ArrayList<>();
        try {
            // Load from csv files to bar arrays
            BarFileLoader.load(Config.getSmallBarsFilePath(), smallBars);
            BarFileLoader.load(Config.getMediumBarsFilePath(), mediumBars);
            BarFileLoader.load(Config.getLargeBarsFilePath(), largeBars);
            // Create dataset for machine learning
            ds = MLDataSetLoader.createBufferedMLDataSet(smallBars, mediumBars, largeBars);
        } finally {
            // Clear arrays to not store in memory
            smallBars.clear();
            mediumBars.clear();
            largeBars.clear();
        }
        
        return ds;
    }

    /**
     * Load to dataset
     */
    public static MLDataSet createBufferedMLDataSet(List<Bar> smallBars, List<Bar> mediumBars, List<Bar> largeBars) throws FileNotFoundException, IOException {
        int windowSize = Config.getSmallBarsWindowSize() + Config.getMediumBarsWindowSize() + Config.getLargeBarsWindowSize();
        
        // Get temp ml data file path
        String fileName = Config.getDataDir() + MLDataSetLoader.class.getName() + ".egb";
        File file = new File(fileName);
        
        // Create new dataset
        BufferedMLDataSet ds = new BufferedMLDataSet(file);
        ds.beginLoad(windowSize,2);
        
        int mediumPos = mediumBars.size() - 1;
        int largePos = largeBars.size() - 1;
        // Go through small pos
        for (int smallPos = smallBars.size() - 1; smallPos >= 0; smallPos--) {
            Bar smallBar = smallBars.get(smallPos);
            Calendar time = smallBar.getTime();
            // Get last positions
            mediumPos = getLastPos(mediumBars, time, mediumPos);
            largePos = getLastPos(mediumBars, time, largePos);

            // Get data windows for small, medium, large bars
            List<Bar> smallWindow = getWindowBars(smallBars, smallPos, Config.getSmallBarsWindowSize());
            List<Bar> mediumWindow = getWindowBars(mediumBars, mediumPos, Config.getMediumBarsWindowSize());
            List<Bar> largeWindow = getWindowBars(largeBars, largePos, Config.getMediumBarsWindowSize());

            // Create resulting window
            List<Bar> window = new ArrayList<>();
            window.addAll(smallWindow);
            window.addAll(mediumWindow);
            window.addAll(largeWindow);
            
            // Create actual and result data for ML
            BasicMLData inputData = barsToMLData(window);

            
        }
        ds.endLoad(); 
        
        return ds;
    }
    
    /**
     * Constructs data array from bar list 
       * @param bars bar list like 3 bars: M1, M15, H1
     * @return 
     */
    private static BasicMLData barsToMLData(List<Bar> bars){
        double[] array = new double[bars.size() * 6];
        int i = 0;
        for(Bar bar: bars){
            // Add bar to array
            array[i++] = (double)(bar.getTime().getTimeInMillis()/1000);
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
     * @param bars
     * @param startIndex
     * @param windowSize
     * @return 
     */
    private static List<Bar> getWindowBars(List<Bar> bars, int endIndex, int windowSize)
    {
        List<Bar> result = new ArrayList<>();
        if(endIndex < windowSize -1)
            return result;
        int startIndex = endIndex - windowSize+1;
        result = bars.subList(startIndex, windowSize);
        return result;
    }
    
    /**
     * Get last pos before upper bound time
     * @param initialPos
     * @param time 
     */
    private static int getLastPos(List<Bar> bars, Calendar upperTime, int initialPos){
        int lastPos = -1;
        int i;
        for(i = initialPos; i >=0; i--){
            Bar bar = bars.get(i);
   
            if(bars.get(i).getTime().getTimeInMillis() < upperTime.getTimeInMillis()){
                break;
            }
        }
        if(i >=0){ 
            lastPos = i;
        }
        return lastPos;
    }

}
