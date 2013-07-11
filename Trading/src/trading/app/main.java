/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.encog.ml.data.MLDataSet;
import trading.data.MLDataSetLoader;

/**
 *
 * @author pdg
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        MLDataSet ds = MLDataSetLoader.createBufferedMLDataSet();
     }
}
