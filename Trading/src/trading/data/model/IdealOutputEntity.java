/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

/**
 * Output entity to form ideal MLData and compare with real output
 * @author dima
 */
public class IdealOutputEntity {
    private BarEntity barEntity;
    private double relativeHigh;
    private double relativeLow;
    
    /**
     * Construct from bar
     * @param entity 
     */
    public IdealOutputEntity(BarEntity entity, double absoluteHigh, double absoluteLow){
        this.barEntity = entity;
        this.relativeHigh =  (absoluteHigh/entity.getAbsoluteBar().getHigh()) -1;
        this.relativeLow =  (absoluteLow/entity.getAbsoluteBar().getLow()) -1;        
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
        return barEntity.getAbsoluteBar().getLow() * (1+relativeLow);
    }
    /**
     * Gets maximum price
     * @return 
     */
    public double getAbsoluteHigh(){
      return barEntity.getAbsoluteBar().getHigh() * (1+relativeHigh);
    }
    /**
     * Gets minimum price change percent
     * @return 
     */
    public double getRelativeLow(){
        return relativeLow;
    }
    /**
     * Gets high price change percent
     * @return 
     */
    public double getRelativeHigh(){
        return relativeHigh;
    }
}
