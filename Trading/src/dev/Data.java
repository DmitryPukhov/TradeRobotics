package dev;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Neural network dat
 *
 * @author pdg
 */
public class Data {
            /**
     * MLDataSet creation
     * @return 
     */
    public static MLDataSet getTrainMLDataSet(){
        
        MLDataSet ds = new BasicMLDataSet(trainInput, trainIdealOutput);
        return ds;

        // Create output data
        
    }
    
    /**
     * Input for train
     */
    private static double[][] trainInput = new double[][]{
        {0.01, 0.02, 0.03},
        {0.02, 0.03, 0.04},
        {0.03, 0.04, 0.05},
        {0.04, 0.05, 0.06},
        {0.05, 0.06, 0.07},
        {0.06, 0.07, 0.08},
        {0.07, 0.08, 0.09},
        {0.08, 0.09, 0.1},
        {0.09, 0.1, 0.11}        
    };
    /**
     * Ideal output for train
     */
    private static double[][] trainIdealOutput = new double[][]{
        {0.04},
        {0.05},
        {0.06},
        {0.07},
        {0.08},
        {0.09},
        {0.1},
        {0.11},
        {0.12}
    };
    /**
     * Input for test
     */
    public static double[][] testInput = new double[][]{
        {0.9, 0.9, 0.9},
        {0.11, 0.12, 0.13},
        {0.12, 0.13, 0.14},
        {0.13, 0.14, 0.15}
    };
    /**
     * Ideal for test
     */
    public static double[][] testIdealOutput = new double[][]{
        {0.13},
        {0.14},
        {0.15},
        {0.16}
    };
}
