/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Input data entities for network
 * @author dima
 */
public class InputEntity {
    private List<BarEntity> smallBars = new ArrayList<>();
    private List<BarEntity> mediumBars = new ArrayList<>();
    private List<BarEntity> largeBars = new ArrayList<>();
    
    /**
     * Construct input data from small, medium, large bars
     * @param smallBars
     * @param mediumBars
     * @param largeBars 
     */
    public InputEntity(List<BarEntity> smallBars, List<BarEntity> mediumBars, List<BarEntity> largeBars){
        this.smallBars = smallBars;
        this.mediumBars = mediumBars;
        this.largeBars = largeBars;
    }
    
    /**
     * Small period bars
     * @return 
     */
    public List<BarEntity> getSmallBars() {
        return smallBars;
    }

    /**
     * Medium period bars
     * @return 
     */
    public List<BarEntity> getMediumBars() {
        return mediumBars;
    }

    /**
     * Large period bars
     * @return 
     */
    public List<BarEntity> getLargeBars() {
        return largeBars;
    }
}
