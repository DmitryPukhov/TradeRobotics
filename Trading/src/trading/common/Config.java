/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.common;

import trading.data.Bar;

/**
 * Application configuration data
 *
 * @author pdg
 */
public class Config {
    private static final String dataDir = "data/";
    private static final String smallBarsFileName = "SPFB.RTS_130711_130711_M1.csv";
    private static final String mediumBarsFileName = "SPFB.RTS_130611_130711_M15.csv";
    private static final String largeBarsFileName = "SPFB.RTS_130611_130711_D1.csv";

    
    private static int smallBarsWindowSize = 15;
    private static int mediumBarsWindowSize = 10;
    private static int largeBarsWindowSize = 5;
    private static long predictionIntervalMillis = 1000 * 60 * 15;// 15 minutes
    private static int outputSize = 2;
    private static int hidden1Count = getInputSize() * 3;
    private static int hidden2Count = 0;

    /**
     * Neurons in first hidden layer
     * @return 
     */
    public static int getHidden1Count() {
        return hidden1Count;
    }
    /**
     * Neurons in second hidden layer
     * @return 
     */
    public static int getHidden2Count() {
        return hidden2Count;
    }
    

    /**
     * Neural network output layer size
     * @return 
     */
    public static int getOutputSize() {
        return outputSize;
    }
  
    /**
     * Gets neural network input layer size
     * @return 
     */
    public static int getInputSize(){
        return (smallBarsWindowSize + mediumBarsWindowSize + largeBarsWindowSize) * Bar.FIELD_COUNT;
    }
    public static long getPredictionIntervalMillis() {
        return predictionIntervalMillis;
    }
    
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
