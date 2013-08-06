/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dev;

import java.awt.Color;
import java.util.logging.Logger;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.genetic.MLMethodGenomeFactory;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.AnalyzeNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import trading.common.NeuralContext.Training;
import org.jfree.chart.ChartPanel;
/**
 *
 * @author pdg
 */
public class Frame1 extends javax.swing.JFrame {
    JFreeChart chart;
    XYSeries trainSeries;
    XYSeries testSeries;
    BasicNetwork network;
    
    /**
    * Create a new network
    * @return 
    */
   public  BasicNetwork createNetwork(int[] layers) {

       //int[] layers = getLayers();
       if(layers.length < 2){
           throw new IllegalArgumentException("Wrong network neurons in layers");
       }
        final FeedForwardPattern pattern = new FeedForwardPattern();

        // Set layers
        pattern.setInputNeurons(layers[0]);
        for(int i = 1; i < layers.length - 1; i++){
            int neurons = layers[i];
            if(neurons == 0){
                continue;
            }
            pattern.addHiddenLayer(neurons);
        }

        pattern.setOutputNeurons(layers[layers.length-1]);
        
        // Activation functioni
        //pattern.setActivationFunction(new ActivationTANH());
        pattern.setActivationFunction(new ActivationLinear());
        //pattern.setActivationFunction(new ActivationElliott());

        // Create network
        final BasicNetwork network = (BasicNetwork) pattern.generate();
        network.reset();
 	(new ConsistentRandomizer(-1,1,100)).randomize(network);       
        updateNetworkInfo(network);
        
        return network;
    }   
   
   /**
    * Get layers neurons count array
    * @return 
    */
   public int[] getLayers(){
       // Split layers text field and convert to int array
       String[] stringValues = layersTextField.getText().split(",");
       int[] layers = new int[stringValues.length];
       for(int i = 0; i < stringValues.length; i++){
           String stringVal = stringValues[i];
           layers[i] = Integer.parseInt(stringVal);
       }
       return layers;
   }
   /**
    * Train using Data class
    * @param network
    * @param ds 
    */
   public void trainNetwork(BasicNetwork network, MLDataSet ds){

           // train the neural network
       int epoches = Integer.parseInt(epochCountTextField.getText());
 
        ResilientPropagation train = new ResilientPropagation(network, ds);
       // train.setThreadCount(10);
        for (int epoch = 0; epoch < epoches; epoch++) {
            // Print info
                 // Iteration
            train.iteration();

            updateNetworkInfo(network);
            // Print info
            // Calculate error
            double error = train.getError();
            lastErrorLabel.setText(Double.toString(error));
   
            Logger.getLogger(Frame1.class.getName()).info(String.format("Epoch %d. Error %s", epoch, Double.toString(error)));
        }
        //train.finishTraining();
        
        updateNetworkInfo(network);

   }
   private void updateNetworkInfo(BasicNetwork network){
         // Display weights info
        String weightsInfo = getNetworkInfo(network);
        notZeroWeightsLabel.setText(weightsInfo);   
        
             String weights = network.dumpWeights();
            weightsTextArea.setText(weights);       
   }
   
   /**
    * Test with test data
    * @param network 
    */
   public void testNetwork(BasicNetwork network, MLDataSet ds){
       //Data.getTestMLDataSet();
       int base = 101;
       for(int i = 0; i < ds.size(); i++){
           double[] input = ds.get(i).getInputArray();
           MLData inputData = new BasicMLData(input);
           double[] ideal = ds.get(i).getIdealArray();
           MLData outputData = network.compute(inputData);
          
           double value = outputData.getData(0);
           double idealValue = ideal[0];
           addChartValue(idealValue, false);
           addChartValue(value, true);

           Logger.getLogger(Frame1.class.getName()).info(String.format("Iteration %d. output %s", i, Double.toString(value)));           
       }
   }
   
   /**
    * Network info string: layers, info about weights
    * @param network
    * @return 
    */
   private String getNetworkInfo(BasicNetwork network){
       AnalyzeNetwork a = new AnalyzeNetwork(network);
       int connections = a.getTotalConnections();
       int disabledConnections = a.getDisabledConnections();
       String connectionsInfo = String.format("Connections: %d, disabled: %d. ", connections, disabledConnections);
       
       
       int totalWeights = a.getWeightValues().length;
       int zeroWeights = 0;
       for(double w: a.getWeightValues()){
           if(w > -0.00000000001 && w < 0.00000000001){
               zeroWeights ++;
           }
       }
       String weightsInfo = String.format("Weights: %d, zero weights: %d.", totalWeights, zeroWeights);
       return connectionsInfo + weightsInfo;

//               
//       
//       int totalWeights = 0;
//       StringBuilder sb = new StringBuilder("Not zero weights:");
//       for(int l= 0; l < network.getLayerCount()-1; l++){
//           int notZeroCount = 0;
//           for(int n = 0; n < network.getLayerNeuronCount(l); n++){
//               for(int nn = 0; nn < network.getLayerNeuronCount(l+1); nn++){
//                   double weight = network.getWeight(l, n, nn);
//       
//                   totalWeights ++;
//                   if((-0.00000000001 > weight) ||(weight > 0.00000000001)){
//                       notZeroCount++;
//                   }
//               }
//           }
//           sb.append(notZeroCount).append(",");
//       }
//       sb.insert(0, String.format("Total weights: %s. ", totalWeights));
//       String infoString = sb.toString().replaceAll(",$", "");
//       return infoString;
   }
     
    /**
     * Creates new form Frame1
     */
    public Frame1() {
        initChart();
        initComponents();
    }
     /**
     * Init JFreeChart
     */
    private void initChart() {
        // Create dataset and series
        TableXYDataset ds = new DefaultTableXYDataset();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        trainSeries = new XYSeries("Ideal");
        testSeries = new XYSeries("Test");
        
        xySeriesCollection.addSeries(trainSeries);
        xySeriesCollection.addSeries(testSeries);
        // Create chartAbsolute
        chart = ChartFactory.createXYLineChart("Price values", "Iteration", "Price", xySeriesCollection, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRangeAxis().setAutoRange(true);
        // Auto range
        NumberAxis valueAxis = (NumberAxis) plot.getRangeAxis();
        valueAxis.setAutoRangeIncludesZero(false);
        // Set line colors
        plot.getRenderer().setSeriesPaint(0, Color.GREEN);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);


        //plot.getDomainAxis().setRange(1, (double)NeuralContext.Test.getMaxIterationCount());
    }
    
    private void addChartValue(double value, boolean isTest){
        XYSeries series = (isTest)? testSeries : trainSeries;
        series.getItemCount();
        series.add(series.getItemCount(), value);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        trainButton = new javax.swing.JButton();
        layersTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        epochCountTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        notZeroWeightsLabel = new javax.swing.JLabel();
        chartPanel = new ChartPanel(chart);
        jScrollPane1 = new javax.swing.JScrollPane();
        weightsTextArea = new javax.swing.JTextArea();
        createButton = new javax.swing.JButton();
        lastErrorLabel = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        trainButton.setText("Train");
        trainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainButtonActionPerformed(evt);
            }
        });

        layersTextField.setText("10,10,10");

        jLabel2.setText("Neurons:");

        epochCountTextField.setText("10");

        jLabel3.setText("Epoches");

        notZeroWeightsLabel.setText("Weights:");

        weightsTextArea.setColumns(20);
        weightsTextArea.setRows(5);
        jScrollPane1.setViewportView(weightsTextArea);

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(32, 32, 32))
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chartPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(191, Short.MAX_VALUE))
        );

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        lastErrorLabel.setText("Last error:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(layersTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(epochCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(notZeroWeightsLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lastErrorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(trainButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(509, 509, 509)
                        .addComponent(createButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(layersTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(epochCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(notZeroWeightsLabel)
                        .addGap(55, 55, 55))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(trainButton)
                            .addComponent(lastErrorLabel))
                        .addGap(18, 18, 18)))
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void trainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainButtonActionPerformed
        testSeries.clear();
        trainSeries.clear();
        
        int[] layers = getLayers();
        Data.initRandomData(layers[0],layers[layers.length-1],Context.samples, Context.testSamples);
        
        MLDataSet ds = Data.getTrainMLDataSet();
        MLDataSet testDs = Data.getTestMLDataSet();

       // network =  createNetwork(layers);
        trainNetwork(network, ds);
        testNetwork(network, testDs);
    }//GEN-LAST:event_trainButtonActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        
        int[] layers = getLayers();
        network = createNetwork(layers);
        testSeries.clear();
        trainSeries.clear();       
    }//GEN-LAST:event_createButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame1().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton createButton;
    private javax.swing.JTextField epochCountTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lastErrorLabel;
    private javax.swing.JTextField layersTextField;
    private javax.swing.JLabel notZeroWeightsLabel;
    private javax.swing.JButton trainButton;
    private javax.swing.JTextArea weightsTextArea;
    // End of variables declaration//GEN-END:variables
}
