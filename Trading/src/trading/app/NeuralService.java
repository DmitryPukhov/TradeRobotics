/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.Stopwatch;
import org.encog.util.simple.EncogUtility;
import trading.common.Config;
import trading.data.MLDataLoader;

/**
 *
 * @author pdg
 */
public class NeuralService {
   public static void main(String[] args) throws FileNotFoundException, IOException {
       trainNetwork();
   }
   
   /**
    * Creates and returns a trading network
    */
   public static BasicNetwork getNetwork(){
		final FeedForwardPattern pattern = new FeedForwardPattern();
		// Set layers
                
                pattern.setInputNeurons(Config.getInputSize());
                pattern.addHiddenLayer(Config.getHidden1Count());
                //pattern.addHiddenLayer(Config.getHidden2Count());
		pattern.setOutputNeurons(Config.getOutputSize());
                // Activation functioni
                //pattern.setActivationFunction(new ActivationTANH());
                pattern.setActivationFunction(new ActivationLinear());
                //pattern.setActivationFunction(new ActivationElliott());
                
                // Create network
		final BasicNetwork network = (BasicNetwork)pattern.generate();
		network.reset();
		return network;
   }
   
    /**
     * @param args the command line arguments
     */
    public static void trainNetwork() throws FileNotFoundException, IOException {
        Stopwatch watch = new Stopwatch();
        watch.start();
        // Training dataset
        MLDataSet ds = MLDataLoader.getMLDataSet();
        
        
        
        watch.stop();
        Logger.getLogger(NeuralService.class.getName()).info(String.format("Create dataset: %d sec.", watch.getElapsedMilliseconds() / 1000));
        watch.reset();

        watch.start();
        // Network
        final BasicNetwork network = getNetwork();
        watch.stop();
        Logger.getLogger(NeuralService.class.getName()).info(String.format("Create network: %d sec.", watch.getElapsedMilliseconds()/1000));
        watch.reset();
        // Backpropagation training
        ResilientPropagation train = new ResilientPropagation(network, ds);
        //Backpropagation train = new Backpropagation(network, ds);
        train.setThreadCount(10);
 
        Logger.getLogger(NeuralService.class.getName()).info("Start training");
        int epochCount = 30;
        for (int epoch = 0; epoch < epochCount; epoch++) {
            // Print info
            watch.reset();
            watch.start();
            // Iteration
            train.iteration();

            // Print info
            watch.stop();
            float error = (float)train.getError();
            int errorInt = (int)error;
            Logger.getLogger(NeuralService.class.getName()).info(String.format("Epoch %d. Time %d sec, error %s", epoch, watch.getElapsedMilliseconds() / 1000, Double.toString(error)));
        }
        train.finishTraining();
        

    }
}
