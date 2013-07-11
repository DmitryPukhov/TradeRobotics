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
import org.encog.ml.data.buffer.BufferedMLDataSet;
import trading.common.Config;

/**
 * Load bar data to dataset
 * @author pdg
 */
public class MLDataSetLoader {


    /**
     * Load to dataset
     */
    public static void load(List<Bar> smallBars, List<Bar> mediumBars, List<Bar> largeBars) throws FileNotFoundException, IOException {

        try {
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
            }

        } finally {
            smallBars.clear();
            mediumBars.clear();
            largeBars.clear();
        }
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
        Bar lastBar = null;
        int i;
        for(i = initialPos; i >=0; i--){
            Bar bar = bars.get(i);
   
            if(bars.get(i).getTime().getTimeInMillis() < upperTime.getTimeInMillis()){
                break;
            }
        }
        if(i >=0){ initialPos = i;};
        return initialPos;
    }

}
