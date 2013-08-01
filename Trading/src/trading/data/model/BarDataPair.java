/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Pair of input and ideal output data
 * @author dima
 */
public class BarDataPair {
    private BarInputEntity  inputEntity;
    private BarIdealOutputEntity outputEntity;
    
    /**
     * Construct pair from input and output data
     * @param input
     * @param output 
     */
    public BarDataPair(BarInputEntity input, BarIdealOutputEntity output){
       inputEntity = input;
       outputEntity = output;
    }

    public BarInputEntity getInputEntity() {
        return  inputEntity;
    }

    public BarIdealOutputEntity getOutputEntity() {
        return outputEntity;
    }
}
