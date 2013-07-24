/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Pair of input and ideal output data
 * @author dima
 */
public class EntityPair {
    private InputEntity inputData;
    private OutputEntity outputData;
    
    /**
     * Construct pair from input and output data
     * @param input
     * @param output 
     */
    public EntityPair(InputEntity input, OutputEntity output){
        inputData = input;
        outputData = output;
    }

    public InputEntity getInputData() {
        return inputData;
    }

    public OutputEntity getOutputData() {
        return outputData;
    }
}
