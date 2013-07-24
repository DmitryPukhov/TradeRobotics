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
    private List<RelativeBar> smallBars = new ArrayList<>();
    private List<RelativeBar> mediumBars = new ArrayList<>();
    private List<RelativeBar> largeBars = new ArrayList<>();
    
    /**
     * Construct input data from small, medium, large bars
     * @param smallBars
     * @param mediumBars
     * @param largeBars 
     */
    public InputEntity(List<RelativeBar> smallBars, List<RelativeBar> mediumBars, List<RelativeBar> largeBars){
        this.smallBars = smallBars;
        this.mediumBars = mediumBars;
        this.largeBars = largeBars;
    }
    
    /**
     * Small period bars
     * @return 
     */
    public List<RelativeBar> getSmallBars() {
        return smallBars;
    }

    /**
     * Medium period bars
     * @return 
     */
    public List<RelativeBar> getMediumBars() {
        return mediumBars;
    }

    /**
     * Large period bars
     * @return 
     */
    public List<RelativeBar> getLargeBars() {
        return largeBars;
    }
}
