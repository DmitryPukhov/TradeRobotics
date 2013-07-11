/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import trading.data.BarFileLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import org.encog.neural.networks.BasicNetwork;
import trading.common.Config;
import trading.data.Bar;

/**
 *
 * @author pdg
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Load training data
          // Load bar arrays
            ArrayList<Bar> smallBars = new ArrayList<>();
            ArrayList<Bar> mediumBars = new ArrayList<>();
            ArrayList<Bar> largeBars = new ArrayList<>();
            BarFileLoader.load(Config.getSmallBarsFilePath(), smallBars);
            BarFileLoader.load(Config.getMediumBarsFilePath(), mediumBars);
            BarFileLoader.load(Config.getLargeBarsFilePath(), largeBars);

    }
}
