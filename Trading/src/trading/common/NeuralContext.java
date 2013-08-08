/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import trading.app.NeuralService;
import trading.data.model.Bar;
import trading.data.model.OutputEntity;

/**
 * Neural network related current context
 *
 * @author pdg
 */
public class NeuralContext {

    /**
     * csv bars file names
     */
    public static class Files {

        private static final String dataDir = "data/";
        private static final String networkDir = "network/";
        private static final String smallBarsTrainFileName = "SPFB.RTS_130711_130711_M1_train.csv";
        private static final String mediumBarsTrainFileName = "SPFB.RTS_130611_130711_M15_train.csv";
        private static final String largeBarsTrainFileName = "SPFB.RTS_130611_130711_D1_train.csv";
        private static final String smallBarsTestFileName = "SPFB.RTS_130715_130715_M1_test.csv";
        private static final String mediumBarsTestFileName = "SPFB.RTS_130614_130715_M15_test.csv";
        private static final String largeBarsTestFileName = "SPFB.RTS_130614_130715_D1_test.csv";

        /**
         * Saved network files directory
         * @return 
         */
        public static String getNetworkDir() {
            return networkDir;
        }

        /**
         * Directory with csv files
         *
         * @return
         */
        public static String getDataDir() {
            return dataDir;
        }

        /**
         * Small period bars file (1 minute)
         *
         * @return
         */
        public static String getSmallBarsTrainFilePath() {
            return dataDir + smallBarsTrainFileName;
        }

        /**
         * Medium period bars file (15 minutes)
         *
         * @return
         */
        public static String getMediumBarsTrainFilePath() {
            return dataDir + mediumBarsTrainFileName;
        }

        /**
         * Large period bars file (1 hour)
         *
         * @return
         */
        public static String getLargeBarsTrainFilePath() {
            return dataDir + largeBarsTrainFileName;
        }

        /**
         * Path of file with future small bars
         *
         * @return
         */
        public static String getSmallBarsTestFilePath() {
            return dataDir + smallBarsTestFileName;
        }

        /**
         * Medium period future bars file (15 minutes)
         *
         * @return
         */
        public static String getMediumBarsTestFilePath() {
            return dataDir + mediumBarsTestFileName;
        }

        /**
         * Large period future bars file (1 hour)
         *
         * @return
         */
        public static String getLargeBarsTestFilePath() {
            return dataDir + largeBarsTestFileName;
        }
    }

    /**
     * Network training context
     */
    public static class Training{
       private static PropertyChangeSupport pcs = new PropertyChangeSupport(NeuralContext.Training.class);
        /**
         * Add property change listener for Epoch, Error properties
         * @param propertyName
         * @param listener 
         */
        public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
            pcs.addPropertyChangeListener(propertyName, listener);
        }   
  
        
        private static int epoch;
        private static double error; 
        private static int maxEpochCount = 10;
        private static long epochMilliseconds = 0;
        private static int samplesCount = 0;

        public static int getSamplesCount() {
            return samplesCount;
        }

        public static void setSamplesCount(int samplesCount) {
            int prevValue = Training.samplesCount;
            Training.samplesCount = samplesCount;
            pcs.firePropertyChange(PropertyNames.SAMPLES_COUNT, prevValue, Training.samplesCount);
        }
        
        /**
         * Gets last epoch milliseconds
         * @return 
         */
        public static long getEpochMilliseconds() {
            return epochMilliseconds;
        }
        /**
         * Set last epoch milliseconds, fire property change event
         * @param epochMilliseconds 
         */
        public static void setEpochMilliseconds(long epochMilliseconds) {
            // Set and fire event
            long prevValue = Training.epochMilliseconds;
            Training.epochMilliseconds = epochMilliseconds;
            pcs.firePropertyChange(PropertyNames.EPOCH_MILLISECONDS, prevValue, Training.epochMilliseconds);
        }
 
        public static int getMaxEpochCount() {
            return maxEpochCount;
        }
        /**
         * Max epoches
         * @param maxEpochCount 
         */
        public static void setMaxEpochCount(int maxEpochCount) {
            int oldValue = maxEpochCount;
            Training.maxEpochCount = maxEpochCount;
            pcs.firePropertyChange(PropertyNames.MAX_EPOCH_COUNT, oldValue, Training.maxEpochCount);
        }

        public static int getEpoch() {
            return epoch;
        }

        /**
         * Set current train epoch, fire property changed event
         * @param epoch 
         */
        public static void setEpoch(int epoch) {
            // Set and fire event
            int oldValue = Training.epoch;
            Training.epoch = epoch;
            pcs.firePropertyChange(PropertyNames.EPOCH, oldValue, Training.epoch);
        }

        public static double getError() {
            return error;
        }
        
        /**
         * Set current network error value, fire property change event
         * @param error 
         */
        public static void setError(double error) {
            // Set and fire event
            double oldError = Training.error;
            Training.error = error;
            pcs.firePropertyChange(PropertyNames.ERROR, oldError, Training.error);
        }

    }
    
    /**
     * Current network, learning, prediction
     */
    public static class Network{
        private static PropertyChangeSupport pcs = new PropertyChangeSupport(NeuralContext.Network.class);
        /**
         * Add property change listener for Network, Train properties
         * @param propertyName
         * @param listener 
         */
        public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
            pcs.addPropertyChangeListener(propertyName, listener);
        }        
        
        private static BasicNetwork network;
        private static Train train;

        /**
         * Train object
         * @return 
         */
        public static Train getTrain() {
            return train;
        }
        /**
         * Set current Train object, fire property change event
         * @param train 
         */
        public static void setTrain(Train train) {
            Train oldTrain = Network.train;
            Network.train = train;
            pcs.firePropertyChange(PropertyNames.TRAIN, oldTrain, Network.train);
        }
        
        /**
         * Gets network
         * @return 
         */
        public static BasicNetwork getNetwork() {
            return network;
        }
        /**
         * Set neural network, fire property change event
         * @param network 
         */
        public static void setNetwork(BasicNetwork network) {
            // Set network
            BasicNetwork oldNetwork = Network.network;
            Network.network = network;
            pcs.firePropertyChange(PropertyNames.NETWORK,oldNetwork, Network.network);
        }
    }
    
    /**
     * Neural network configuration data
     *
     * @author pdg
     */
    public static class NetworkSettings {

        private static int smallBarsWindowSize = 12;
        private static int mediumBarsWindowSize = 40;
        private static int largeBarsWindowSize = 7;
        private static long predictionIntervalMillis = 1000 * 60 * 60;// 15 minutes
        private static int outputSize = 2;
        private static int hidden1Count = 3*Bar.FIELD_COUNT;//(smallBarsWindowSize+mediumBarsWindowSize+largeBarsWindowSize)*2;
        private static int hidden2Count = 0;

        /**
         * Neurons in first hidden layer
         *
         * @return
         */
        public static int getHidden1Count() {
            return hidden1Count;
        }

        /**
         * Neurons in second hidden layer
         *
         * @return
         */
        public static int getHidden2Count() {
            return hidden2Count;
        }

        /**
         * Neural network output layer size
         *
         * @return
         */
        public static int getOutputSize() {
            return outputSize;
        }

        /**
         * Gets neural network input layer size
         *
         * @return
         */
        public static int getInputSize() {
            return (smallBarsWindowSize + mediumBarsWindowSize + largeBarsWindowSize) * Bar.FIELD_COUNT;
        }

        public static long getPredictionIntervalMillis() {
            return predictionIntervalMillis;
        }

        public static int getSmallBarsWindowSize() {
            return smallBarsWindowSize;
        }

        public static int getMediumBarsWindowSize() {
            return mediumBarsWindowSize;
        }

        public static int getLargeBarsWindowSize() {
            return largeBarsWindowSize;
        }
    }
    
    /**
     * Neural network test
     */
    public static class Test{
        private static PropertyChangeSupport pcs = new PropertyChangeSupport(Test.class);
          /**
         * Add property change listener for Network, Train properties
         * @param propertyName
         * @param listener 
         */
        public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
            pcs.addPropertyChangeListener(propertyName, listener);
        }    
        
        private static int iteration;
        private static int maxIterationCount;
        private static double error;
        private static OutputEntity idealEntity;
        private static OutputEntity realEntity;
        
        /**
         * Test iterations count
         * @return 
         */
        public static int getMaxIterationCount() {
            return maxIterationCount;
        }
        
        /**
         * Set max iterations count, fire property change event
         * @param maxIterationCount 
         */
        public static void setMaxIterationCount(int maxIterationCount) {
            int oldValue = Test.maxIterationCount;
            Test.maxIterationCount = maxIterationCount;
            pcs.firePropertyChange(PropertyNames.MAX_ITERATION_COUNT, oldValue, Test.maxIterationCount);
        }
        
        /**
         * Current iteration
         * @return 
         */
        public static int getIteration() {
            return iteration;
        }
        /**
         * Set test iteration, fire change event
         * @param iteration 
         */
        public static void setIteration(int iteration) {
            int oldValue = Test.iteration;
            // Set
            Test.iteration = iteration;
            pcs.firePropertyChange(PropertyNames.ITERATION, oldValue, Test.iteration);
        }

        /**
         * Gets output entity
         * @return 
         */
        public static OutputEntity getIdealEntity() {
            return idealEntity;
        }
        /**
         * Sets ideal entity of current test iteration, fires property change event
         * @param idealEntity 
         */
        public static void setIdealEntity(OutputEntity idealEntity) {
            OutputEntity oldValue = Test.idealEntity;
            Test.idealEntity = idealEntity;
            pcs.firePropertyChange(PropertyNames.IDEAL_OUTPUT_ENTITY, oldValue, Test.idealEntity);
        }
        /**
         * Gets real entity of current iteration
         * @return 
         */
        public static OutputEntity getRealEntity() {
            return realEntity;
        }
        
        /**
         * Sets real entity of current test iteration, fires property change event
         * @param realEntity 
         */
        public static void setRealEntity(OutputEntity realEntity) {
            OutputEntity oldValue = Test.realEntity;
            Test.realEntity = realEntity;
            pcs.firePropertyChange(PropertyNames.REAL_OUTPUT_ENTITY, oldValue, Test.realEntity);
        }
    }
}
