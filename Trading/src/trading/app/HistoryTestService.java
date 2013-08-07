/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import trading.data.BarFileLoader;
import trading.common.*;
import trading.data.model.Bar;
        

/**
 * Test on history data
 * @author pdg
 */
public class HistoryTestService {
    public static void testOnHistory() throws FileNotFoundException, IOException{
        List<Bar> smallBars = BarFileLoader.load(NeuralContext.Files.getSmallBarsTestFilePath());
        //List<Bar> mediumBars = BarFileLoader.load(NeuralContext.Files.)
    }
}
