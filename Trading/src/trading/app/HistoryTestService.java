/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import trading.data.BarFileLoader;
import trading.common.*;
import trading.data.MLBarDataConverter;
import trading.data.MLBarDataLoader;
import trading.data.model.Bar;
import trading.data.model.DataPair;
import trading.data.model.OutputEntity;
        

/**
 * Test on history data
 * @author pdg
 */
public class HistoryTestService {
    /**
     * Train on samples window
     * @param pairs
     * @param currentIndex 
     */
    private static void train(List<DataPair> pairs, int currentIndex) throws FileNotFoundException, IOException{
             // Train on previous samples window
            List<DataPair> trainPairs = pairs.subList(currentIndex - NeuralContext.NetworkSettings.getSmallBarsWindowSize()+1, currentIndex+1);
            MLDataSet trainSet = MLBarDataLoader.getMLDataSet(trainPairs);
            NeuralService.trainNetwork(trainSet);       
    }
    /**
     * Test on samples window
     * @param pairs
     * @param currentIndex 
     */
    private static void test(List<DataPair> pairs, int currentIndex){
                BasicNetwork network = NeuralContext.Network.getNetwork();    
            
               // Prepare data for prediction
            DataPair currentPair = pairs.get(currentIndex);
            MLData input = MLBarDataConverter.inputEntityToMLData(currentPair.getInputEntity());
            // Compute network prediction
             MLData output = network.compute(input);
            // Get network output
            OutputEntity idealEntity = currentPair.getOutputEntity();
            OutputEntity predictedEntity = OutputEntity.createFromRelativeData(idealEntity.getCurrentBarEntity(), idealEntity.getFutureIntervalMillis(), output.getData(0), output.getData(1));

            // Store values in context
            NeuralContext.Test.setIdealEntity(idealEntity);
            NeuralContext.Test.setPredictedEntity(predictedEntity); 
    }
    
    /**
     * Test and learn every iteration
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void test() throws FileNotFoundException, IOException{
        BasicNetwork network = NeuralContext.Network.getNetwork();

        // Get entities from csv files
        List<DataPair> pairs = MLBarDataLoader.getTestEntityPairs();
        NeuralContext.Test.setMaxIterationCount(pairs.size());
        int iteration = 1;
        for(int i = NeuralContext.NetworkSettings.getSmallBarsWindowSize(); i < NeuralContext.Test.getMaxIterationCount(); i++){
            // Train on previous data
            train(pairs, i-1);
            // Test on current data
            test(pairs,i);
         
            // Increase iteration
            NeuralContext.Test.setIteration(iteration++);
        };
//        
//        // Go through every input/ideal pair
//        for (DataPair pair : pairs) {
//            // Process every 15 min
////            BarEntity lastSmallBar = pair.getInputEntity().getSmallBars().get(pair.getInputEntity().getSmallBars().size()-1);
////            if(lastSmallBar.getFutureTime().get(Calendar.MINUTE)%15 != 0){
////                continue;
////            }
//                
//            MLData input = MLBarDataConverter.inputEntityToMLData(pair.getInputEntity());
//            // Compute network prediction
//            MLData output = network.compute(input);
//
//            // Get network output
//            OutputEntity idealEntity = pair.getOutputEntity();
//            OutputEntity realEntity = OutputEntity.createFromRelativeData(idealEntity.getCurrentBarEntity(), idealEntity.getFutureIntervalMillis(), output.getData(0), output.getData(1));
//
//            // Store values in context
//            NeuralContext.Test.setIteration(iteration);
//            NeuralContext.Test.setIdealEntity(idealEntity);
//            NeuralContext.Test.setPredictedEntity(realEntity);
//
//            iteration++;
//        }
        //List<Bar> mediumBars = BarFileLoader.load(NeuralContext.Files.)
    }
    
    public static void train(){
        
    }
}
