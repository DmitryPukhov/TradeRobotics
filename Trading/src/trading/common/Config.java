/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.common;

/**
 * Application configuration data
 *
 * @author pdg
 */
public class Config {
    private static final String dataDir = "C:\\Users\\pdg\\Downloads\\";
    private static final String smallBarsFileName = "SPFB.RTS_130708_130708_1min.csv";
    private static final String mediumBarsFileName = "SPFB.RTS_130708_130708_15min.csv";
    private static final String largeBarsFileName = "SPFB.RTS_130708_130708_1day.csv";

    
    private static int smallBarsWindowSize = 15;
    private static int mediumBarsWindowSize = 32;
    private static int largeBarsWindowSize = 100;
    
    public static int getSmallBarsWindowSize() {
        return smallBarsWindowSize;
    }

    public static int getMediumBarsWindowSize() {
        return mediumBarsWindowSize;
    }

    public static int getLargeBarsWindowSize() {
        return largeBarsWindowSize;
    }


    /**
     * Directory with csv files
     * @return 
     */
    public static String getDataDir() {
        return dataDir;
    }
    /**
     * Small period bars file (1 minute)
     * @return 
     */
    public static String getSmallBarsFilePath() {
        return dataDir + smallBarsFileName;
    }
    /**
     * Medium period bars file (15 minutes)
     * @return 
     */
    public static String getMediumBarsFilePath() {
        return dataDir + mediumBarsFileName;
    }   
     /**
     * Large period bars file (1 hour)
     * @return 
     */
    public static String getLargeBarsFilePath() {
        return dataDir + largeBarsFileName;
    }   
}
