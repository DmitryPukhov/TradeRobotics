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
    private IdealOutputEntity outputEntity;
    
    /**
     * Construct pair from input and output data
     * @param input
     * @param output 
     */
    public EntityPair(InputEntity input, IdealOutputEntity output){
       inputEntity = input;
       outputEntity = output;
    }

    public InputEntity getInputEntity() {
        return  inputEntity;
    }

    public IdealOutputEntity getOutputEntity() {
        return outputEntity;
    }
}
