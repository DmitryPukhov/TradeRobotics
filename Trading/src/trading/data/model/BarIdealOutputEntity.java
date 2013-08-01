/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Output entity to form ideal MLData and compare with real output
 * @author dima
 */
public class BarIdealOutputEntity {
    private BarEntity barEntity;
    
    /**
     * Construct from bar
     * @param entity 
     */
    public BarIdealOutputEntity(BarEntity entity){
        this.barEntity = entity;
    }
    /**
     * Bar to get prediction values from.
     * @return 
     */
    public BarEntity getBarEntity() {
        return barEntity;
    }
    
    /**
     * Gets minimum price
     * @return 
     */
    public double getAbsoluteLow(){
        return barEntity.getAbsoluteBar().getLow();
    }
    /**
     * Gets maximum price
     * @return 
     */
    public double getAbsoluteHigh(){
        return barEntity.getAbsoluteBar().getHigh();
    }
    /**
     * Gets minimum price change percent
     * @return 
     */
    public double getRelativeLow(){
        return barEntity.getRelativeBar().getLow();
    }
    /**
     * Gets high price change percent
     * @return 
     */
    public double getRelativeHigh(){
        return barEntity.getRelativeBar().getHigh();
    }
}
