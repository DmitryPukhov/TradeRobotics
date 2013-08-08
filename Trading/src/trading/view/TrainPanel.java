/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import trading.app.NeuralService;
import trading.common.NeuralContext;
import trading.common.PropertyNames;

/**
 * Train view
 * @author pdg
 */
public class TrainPanel extends javax.swing.JPanel {

    /**
     * Creates new form TrainPanel
     */
    public TrainPanel() {
        createChart();

        initComponents();

        init();
    }
    JFreeChart errorChart;
    XYSeries errorXYSeries;

    /**
     * Neural network related initialization
     */
    private void init() {
        // Init progress bar
        learnProgressBar.setMaximum(NeuralContext.Training.getMaxEpochCount());
        learnProgressBar.setStringPainted(true);
        epochCountSpinner.setValue(NeuralContext.Training.getMaxEpochCount());
        
        // Epoch changed
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.EPOCH, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int epoch = (int) evt.getNewValue();
                learnProgressLabel.setText(String.format("Epoch: %d of %d", epoch, NeuralContext.Training.getMaxEpochCount()));
                learnProgressBar.setMaximum(NeuralContext.Training.getMaxEpochCount());   
                learnProgressBar.setValue(epoch);
            }
        });
        // Epoch lifetime in milliseconds changed
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.EPOCH_MILLISECONDS, new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
               long milliseconds = (long)evt.getNewValue();
               epochLifetimeLabel.setText(String.format("Epoch lifetime: %d sec", (int)milliseconds/1000));
            }
        });
        // New training started
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.TRAIN, new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Clear chart of old training
                errorXYSeries.clear();
            }
        });
        // Train lifetime in milliseconds
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.TRAIN_MILLISECONDS, new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                 long seconds = (long)evt.getNewValue()/1000;
                 trainTimeLabel.setText(String.format("Training time: %d sec", seconds));
            }
        });
        // Error changed
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.ERROR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Double value = ((double)evt.getNewValue())/0.01;
                Double value = ((double) evt.getNewValue()) / 0.01;
                lastErrorLabel.setText(String.format("Error: %f%n %%", value));
                errorXYSeries.add(errorXYSeries.getItemCount() + 1, value);
            }
        });
        // Samples changed
        NeuralContext.Training.addPropertyChangeListener(PropertyNames.SAMPLES_COUNT, new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                samplesLabel.setText("Samples: ".concat(evt.getNewValue().toString()));
            }
        });
    }

    /**
     * Init JFreeChart
     */
    private void createChart() {
        // Create dataset and series
        TableXYDataset ds = new DefaultTableXYDataset();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        errorXYSeries = new XYSeries("Error");
        xySeriesCollection.addSeries(errorXYSeries);

        // Create chart
        errorChart = ChartFactory.createXYLineChart("Error value", "Epoch", "Error, %", xySeriesCollection, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) errorChart.getPlot();
        plot.getRangeAxis().setAutoRange(true);
        plot.getDomainAxis().setRange(1, NeuralContext.Training.getMaxEpochCount());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        learnProgressBar = new javax.swing.JProgressBar();
        learnProgressLabel = new javax.swing.JLabel();
        lastErrorLabel = new javax.swing.JLabel();
        chartPanel = new ChartPanel(errorChart);
        trainButton = new javax.swing.JButton();
        epochLifetimeLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        samplesLabel = new javax.swing.JLabel();
        epochCountSpinner = new javax.swing.JSpinner();
        trainTimeLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1024, 768));

        learnProgressLabel.setText("Epoch:");

        lastErrorLabel.setText("Last error:");

        chartPanel.setAlignmentY(0.0F);

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
        );

        trainButton.setText("Train");
        trainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainButtonActionPerformed(evt);
            }
        });

        epochLifetimeLabel.setText("Epoch lifetime:");

        jLabel1.setText("Epoch count max:");

        samplesLabel.setText("Samples:");

        epochCountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                epochCountSpinnerStateChanged(evt);
            }
        });

        trainTimeLabel.setText("Training time:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(135, 135, 135))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(learnProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 867, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 22, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(learnProgressLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(3, 3, 3)
                                .addComponent(epochCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 4, Short.MAX_VALUE)
                                .addComponent(trainButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(samplesLabel)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lastErrorLabel)
                            .addComponent(epochLifetimeLabel)
                            .addComponent(trainTimeLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(learnProgressLabel)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(samplesLabel)
                        .addComponent(epochCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(trainButton)
                    .addComponent(learnProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(epochLifetimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trainTimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastErrorLabel)
                .addGap(29, 29, 29)
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(454, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void trainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainButtonActionPerformed
        // Run train in new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Clear chart
                    errorXYSeries.clear();
                    // Train
                    NeuralService.trainNetwork();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TrainPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TrainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }//GEN-LAST:event_trainButtonActionPerformed

    private void epochCountSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_epochCountSpinnerStateChanged
         // Update max epoch count in neural context
        Object value = epochCountSpinner.getValue() ;
        if(value == null ){
            return;
        }
        Integer valueInt = Integer.parseInt(value.toString());        
        if(valueInt == null){
            return;
        }
        NeuralContext.Training.setMaxEpochCount(valueInt);
    }//GEN-LAST:event_epochCountSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JSpinner epochCountSpinner;
    private javax.swing.JLabel epochLifetimeLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lastErrorLabel;
    private javax.swing.JProgressBar learnProgressBar;
    private javax.swing.JLabel learnProgressLabel;
    private javax.swing.JLabel samplesLabel;
    private javax.swing.JButton trainButton;
    private javax.swing.JLabel trainTimeLabel;
    // End of variables declaration//GEN-END:variables
}
