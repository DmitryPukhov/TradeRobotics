/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Pair of input and ideal output data
 * @author dima
 */
public class DataPair {
    private InputData inputData;
    private OutputData outputData;
    
    /**
     * Construct pair from input and output data
     * @param input
     * @param output 
     */
    public DataPair(InputData input, OutputData output){
        inputData = input;
        outputData = output;
    }

    public InputData getInputData() {
        return inputData;
    }

    public OutputData getOutputData() {
        return outputData;
    }
}
