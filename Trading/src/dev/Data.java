package dev;

import java.util.Random;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Neural network dat
 *
 * @author pdg
 */
public class Data {

    /**
     * Fill array with random dat
     */
    public static void initRandomData(double[][] array, int size, int samples) {
        // Init random data
        Random r = new Random();
        for (int i = 0; i < samples; i++) {
            double sum = 0;
            for (int j = 0; j < size; j++) {
                array[i][j] = r.nextDouble();
                sum += array[i][j];
            }
        }
    }
    /**
     * Fill input and output arrays with random data
     * @param input
     * @param inputSize
     * @param output
     * @param outputSize
     * @param trainSamples, int testSamples 
     */
    public static void initRandomData(int inputSize, int outputSize, int trainSamples, int testSamples){
        trainInput = new double[trainSamples][inputSize];
        trainIdealOutput = new double[trainSamples][outputSize];
        testInput = new double[testSamples][inputSize];
        testIdealOutput = new double[testSamples][outputSize];              
        initRandomData(trainInput, inputSize, trainSamples);
        initRandomData(trainIdealOutput, outputSize, trainSamples);
       // trainIdealOutput = getOutput(trainInput);
//        initRandomData(trainIdealOutput, outputSize, trainSamples);
 
        
        initRandomData(testInput, inputSize, testSamples);
        initRandomData(testIdealOutput, outputSize, testSamples);
        //testIdealOutput = getOutput(testInput);
 //       initRandomData(testIdealOutput, outputSize, testSamples);      
    }
    /**
     * Init output for input data
     * @param input
     * @param output
     * @return 
     */
    public static double[][] getOutput(double[][]input, int outputSize){
        double[][] output = new double[input.length][outputSize];
        for(int i = 0; i < input.length; i++){
            double sum = 0;
            for(int j = 0; j < input[i].length; j++){
                sum += input[i][j];
            }
            double avg = sum/input[i].length;
            output[i][0] = avg;
        }
        return output;
    }
    public static double getAverage(double[] data){
       double sum = 0;
       for(double val: data){
           sum+=val;
       }
       double avg=sum/data.length;
       return avg;
    }

    /**
     * Get random train data
     */
    public static MLDataSet getTrainMLDataSet() {
        return new BasicMLDataSet(trainInput, trainIdealOutput);
    }

    /**
     * Get random train data
     */
    public static MLDataSet getTestMLDataSet() {
        return new BasicMLDataSet(testInput, testIdealOutput);
    }

    /**
     * Input for train
     */
    private static double[][] trainInput;
    /**
     * Ideal output for train
     */
    private static double[][] trainIdealOutput;
    /**
     * Input for test
     */
    public static double[][] testInput;
    /**
     * Ideal for test
     */
    public static double[][] testIdealOutput;
}
