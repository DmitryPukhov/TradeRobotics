/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.Stopwatch;
import org.encog.util.simple.EncogUtility;
import trading.common.Config;
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
        Stopwatch watch = new Stopwatch();
        watch.start();
        // Training dataset
        MLDataSet ds = MLDataSetLoader.createBufferedMLDataSet();

        watch.stop();
        System.out.println(String.format("Create dataset: %d sec.", watch.getElapsedMilliseconds() / 1000));
        watch.reset();

        // Network
        final BasicNetwork network = EncogUtility.simpleFeedForward(
                Config.getInputSize(),
                Config.getHidden1Count(),
                Config.getHidden2Count(),
                Config.getOutputSize(),
                false);
        // Backpropagation training
        ResilientPropagation train = new ResilientPropagation(network, ds);
        //Backpropagation train = new Backpropagation(network, ds);
        train.setThreadCount(10);

        System.out.println("Start training");
        int epochCount = 3;
        for (int epoch = 0; epoch < epochCount; epoch++) {
            // Print info
            watch.reset();
            watch.start();
            // Iteration
            train.iteration();
            
            // Print info
            watch.stop();
            float error = (float)train.getError();
         
            System.out.println(String.format("Epoch %d. Time %d sec, error %s", epoch, watch.getElapsedMilliseconds() / 1000, Double.toString(error)));
        }
    }
}
