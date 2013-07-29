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
        private static final String smallBarsFileName = "SPFB.RTS_130711_130711_M1.csv";
        private static final String mediumBarsFileName = "SPFB.RTS_130611_130711_M15.csv";
        private static final String largeBarsFileName = "SPFB.RTS_130611_130711_D1.csv";
        private static final String futureSmallBarsFileName = "SPFB.RTS_130715_130715_M1_future.csv";
        private static final String futureMediumBarsFileName = "SPFB.RTS_130614_130715_M15_future.csv";
        private static final String futureLargeBarsFileName = "SPFB.RTS_130614_130715_D1_future.csv";

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
        public static String getSmallBarsFilePath() {
            return dataDir + smallBarsFileName;
        }

        /**
         * Medium period bars file (15 minutes)
         *
         * @return
         */
        public static String getMediumBarsFilePath() {
            return dataDir + mediumBarsFileName;
        }

        /**
         * Large period bars file (1 hour)
         *
         * @return
         */
        public static String getLargeBarsFilePath() {
            return dataDir + largeBarsFileName;
        }

        /**
         * Path of file with future small bars
         *
         * @return
         */
        public static String getFutureSmallBarsFilePath() {
            return dataDir + futureSmallBarsFileName;
        }

        /**
         * Medium period future bars file (15 minutes)
         *
         * @return
         */
        public static String getFutureMediumBarsFilePath() {
            return dataDir + futureMediumBarsFileName;
        }

        /**
         * Large period future bars file (1 hour)
         *
         * @return
         */
        public static String getFutureLargeBarsFilePath() {
            return dataDir + futureLargeBarsFileName;
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
        private static float error;        

        public static int getEpoch() {
            return epoch;
        }

        public static void setEpoch(int epoch) {
            // Set and fire event
            int oldValue = Training.epoch;
            Training.epoch = epoch;
            pcs.firePropertyChange("Epoch", oldValue, Training.epoch);
        }

        public static float getError() {
            return error;
        }

        public static void setError(float error) {
            // Set and fire event
            float oldError = Training.error;
            Training.error = error;
            pcs.firePropertyChange("Error", oldError, Training.error);
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

        public static Train getTrain() {
            return train;
        }

        public static void setTrain(Train train) {
            Train oldTrain = Network.train;
            Network.train = train;
            pcs.firePropertyChange("Train", oldTrain, Network.train);
        }

        public static BasicNetwork getNetwork() {
            return network;
        }

        public static void setNetwork(BasicNetwork network) {
            // Set network
            BasicNetwork oldNetwork = Network.network;
            Network.network = network;
            pcs.firePropertyChange("Network",oldNetwork, Network.network);
        }
    }
    
    /**
     * Neural network configuration data
     *
     * @author pdg
     */
    public static class NetworkSettings {

        private static int smallBarsWindowSize = 15;
        private static int mediumBarsWindowSize = 40;
        private static int largeBarsWindowSize = 10;
        private static long predictionIntervalMillis = 1000 * 60 * 15;// 15 minutes
        private static int outputSize = 2;
        private static int hidden1Count = getInputSize() * 3;
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
}
