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
    private InputEntity  inputEntity;
    private OutputEntity outputEntity;
    
    /**
     * Construct pair from input and output data
     * @param input
     * @param output 
     */
    public EntityPair(InputEntity input, OutputEntity output){
       inputEntity = input;
       outputEntity = output;
    }

    public InputEntity getInputEntity() {
        return  inputEntity;
    }

    public OutputEntity getOutputEntity() {
        return outputEntity;
    }
}
