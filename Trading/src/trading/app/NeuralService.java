/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Stopwatch;
import org.encog.util.simple.EncogUtility;
import trading.common.NeuralContext;
import trading.data.MLBarDataConverter;
import trading.data.MLBarDataLoader;
import trading.data.model.BarEntity;
import trading.data.model.DataPair;
import trading.data.model.IdealOutputEntity;
import trading.data.model.RealOutputEntity;

/**
 * Neural network service
 *
 * @author pdg
 */
public class NeuralService {

    /**
     * Create network with layers
     *
     * @param layers
     * @return
     */
    public static BasicNetwork createNetwork(List<Integer> layers) {
        if (layers.size() < 2) {
            throw new IllegalArgumentException("Wrong network layers count");
        }
        final FeedForwardPattern pattern = new FeedForwardPattern();
        // Input neurons
        int input = layers.get(0);
        pattern.setInputNeurons(input);
        // Hidden neurons
        for (int i = 1; ((i < layers.size() - 1)); i++) {
            int neurons = layers.get(i);
            if (neurons > 0) {
                pattern.addHiddenLayer(layers.get(i));
            }
        }
        // Output neurons
        int output = layers.get(layers.size() - 1);
        pattern.setOutputNeurons(output);

//        // Activation functioni
//        pattern.setActivationFunction(new ActivationTANH());
        pattern.setActivationFunction(new ActivationLinear());
//        //pattern.setActivationFunction(new ActivationElliott()); 

        // Create network
        final BasicNetwork network = (BasicNetwork) pattern.generate();
        // Randomize the network
        (new ConsistentRandomizer(-1, 1, 100)).randomize(network);
        NeuralContext.Network.setNetwork(network);

        return network;


    }

    /**
     * Creates and returns a trading network
     */
//    public static BasicNetwork createNetwork() {
//        Stopwatch watch = new Stopwatch();
//        final FeedForwardPattern pattern = new FeedForwardPattern();
//        // Input layer
//        pattern.setInputNeurons(NeuralContext.NetworkSettings.getInputSize());
//
//        // Hidden layer 1
//        int hidden1Count = NeuralContext.NetworkSettings.getHidden1Count();
//        if (hidden1Count > 0) {
//            pattern.addHiddenLayer(NeuralContext.NetworkSettings.getHidden1Count());
//        }
//        // Hidden layer 2
//        int hidden2Count = NeuralContext.NetworkSettings.getHidden2Count();
//        if (hidden2Count > 0) {
//            pattern.addHiddenLayer(NeuralContext.NetworkSettings.getHidden2Count());
//        }
//        // Output layer
//        pattern.setOutputNeurons(NeuralContext.NetworkSettings.getOutputSize());
//
//
//        // Activation functioni
//        //pattern.setActivationFunction(new ActivationTANH());
//        pattern.setActivationFunction(new ActivationLinear());
//        //pattern.setActivationFunction(new ActivationElliott());
//
//        // Create network
//        final BasicNetwork network = (BasicNetwork) pattern.generate();
//        network.reset();
//
//        watch.stop();
//        Logger.getLogger(NeuralService.class.getName()).info(String.format("Create network: %d sec.", watch.getElapsedMilliseconds() / 1000));
//        watch.reset();
//
//        NeuralContext.Network.setNetwork(network);
//
//        return network;
//    }
    /**
     * NetworkSettings learning
     */
    public static void trainNetwork() throws FileNotFoundException, IOException {
        BasicNetwork network = NeuralContext.Network.getNetwork();

        Stopwatch watch = new Stopwatch();
        watch.start();
        // Training dataset
        MLDataSet ds = MLBarDataLoader.getMLDataSet();
        NeuralContext.Training.setSamplesCount(ds.size());


        watch.stop();
        Logger.getLogger(NeuralService.class.getName()).info(String.format("Create dataset: %d sec.", watch.getElapsedMilliseconds() / 1000));
        watch.reset();

        // Backpropagation training
        ResilientPropagation train = new ResilientPropagation(network, ds);


        //Backpropagation train = new Backpropagation(network, ds);
        //train.setThreadCount(10);

        Logger.getLogger(NeuralService.class.getName()).info("Start training");

        for (int epoch = 1; epoch <= NeuralContext.Training.getMaxEpochCount(); epoch++) {
            // Print info
            watch.reset();
            watch.start();
            // Iteration
            train.iteration();

            // Print info
            watch.stop();
            // Calculate error
            double error = train.getError();

            //NeuralContext.Training.setError(error);
            // Error change event
            // Epoch change event

            NeuralContext.Training.setEpoch(epoch);
            NeuralContext.Training.setEpochMilliseconds(watch.getElapsedMilliseconds());
            NeuralContext.Training.setError(error);
            Logger.getLogger(NeuralService.class.getName()).info(String.format("Epoch %d. Time %d sec, error %s", epoch, watch.getElapsedMilliseconds() / 1000, Double.toString(error)));
        }
        train.finishTraining();

    }

    /**
     * Predict results
     */
    public static void testNetwork() throws FileNotFoundException, IOException {
        BasicNetwork network = NeuralContext.Network.getNetwork();

        // Get entities from csv files
        List<DataPair> pairs = MLBarDataLoader.getEntityPairs(NeuralContext.Files.getFutureSmallBarsFilePath(), NeuralContext.Files.getFutureMediumBarsFilePath(), NeuralContext.Files.getFutureLargeBarsFilePath());
        NeuralContext.Test.setMaxIterationCount(pairs.size());

        int iteration = 1;
        // Go through every input/ideal pair
        for (DataPair pair : pairs) {
            // Process every 15 min
//            BarEntity lastSmallBar = pair.getInputEntity().getSmallBars().get(pair.getInputEntity().getSmallBars().size()-1);
//            if(lastSmallBar.getTime().get(Calendar.MINUTE)%15 != 0){
//                continue;
//            }
                
            MLData input = MLBarDataConverter.inputEntityToMLData(pair.getInputEntity());
            // Compute network prediction
            MLData output = network.compute(input);

            // Get network output
            IdealOutputEntity idealEntity = pair.getOutputEntity();
            RealOutputEntity realEntity = new RealOutputEntity(idealEntity.getBarEntity().getPreviousAbsoluteBar(), idealEntity.getBarEntity().getTime(), output.getData(0), output.getData(1));

            // Store values in context
            NeuralContext.Test.setIteration(iteration);
            NeuralContext.Test.setIdealEntity(idealEntity);
            NeuralContext.Test.setRealEntity(realEntity);

            iteration++;
        }
    }

    /**
     * Saves current network to file
     *
     * @param fileName
     */
    public static void saveNetwork(File file) {
        EncogDirectoryPersistence.saveObject(file, NeuralContext.Network.getNetwork());
    }

    /**
     * Load network from file
     *
     * @param fileName
     */
    public static void loadNetwork(File file) {
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(file);
        NeuralContext.Network.setNetwork(network);
    }

    /**
     * Reset network weights
     */
    public static void resetNetwork() {
        NeuralContext.Network.getNetwork().reset();
    }
}
