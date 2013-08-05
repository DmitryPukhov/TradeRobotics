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
     * Arrays initialization
     */
    public static void initRandomData() {
        trainInput = new double[Context.samples][Context.inputSize];
        trainIdealOutput = new double[Context.samples][Context.outputSize];
        testInput = new double[Context.testSamples][Context.inputSize];
        testIdealOutput = new double[Context.testSamples][Context.outputSize];

        initRandomData(trainInput, trainIdealOutput, Context.samples);
        initRandomData(testInput, testIdealOutput, Context.testSamples);
    }

    /**
     * Data sets initialization
     */
    public static void initRandomData(double[][] input, double[][] output, int size) {

        // Init random data
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            double sum = 0;
            for (int j = 0; j < Context.inputSize; j++) {
                input[i][j] = r.nextDouble();
                sum += input[i][j];
            }
            output[i][0] = sum / Context.inputSize;
        }
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
    public static MLDataSet getTestMLDataSet(int samples) {
        return new BasicMLDataSet(testInput, testIdealOutput);
    }

    /**
     * MLDataSet creation
     *
     * @return
     */
    public static MLDataSet getTrainMLDataSet(int samples, double[][] input, double[][] idealOutput) {


        Random r = new Random();
        input = new double[samples][Context.inputSize];
        idealOutput = new double[samples][Context.outputSize];

        for (int i = 0; i < samples; i++) {
            double sum = 0;
            for (int j = 0; j < Context.inputSize; j++) {
                input[i][j] = r.nextDouble();
                sum += input[i][j];
            }
            idealOutput[i][0] = sum / Context.inputSize;
        }

        MLDataSet ds = new BasicMLDataSet(input, idealOutput);
        return ds;

        // Create output data

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
