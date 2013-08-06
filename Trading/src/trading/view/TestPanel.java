/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.view;

import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import trading.app.NeuralService;
import trading.common.NeuralContext;
import trading.common.PropertyNames;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYDataItem;

/**
 * Neural network testNetwork panel
 *
 * @author pdg
 */
public class TestPanel extends javax.swing.JPanel {

    JFreeChart chartAbsolute;
    XYSeries idealHighSeriesAbsolute;
    XYSeries idealLowSeriesAbsolute;
    XYSeries realHighSeriesAbsolute;
    XYSeries realLowSeriesAbsolute;
    JFreeChart chartRelative;
    XYSeries idealHighSeriesRelative;
    XYSeries idealLowSeriesRelative;
    XYSeries realHighSeriesRelative;
    XYSeries realLowSeriesRelative;
    /**
     * Creates new form TestPanel
     */
    public TestPanel() {
        // Test results chartAbsolute creation
        createChartAbsolute();
        createChartRelative();
                
        initComponents();

        // Neural network related initialization
        init();
    }

   /**
     * Init JFreeChart
     */
    private void createChartRelative() {
        // Create dataset and series
        TableXYDataset ds = new DefaultTableXYDataset();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        idealHighSeriesRelative = new XYSeries("Ideal High");
        idealLowSeriesRelative = new XYSeries("Ideal Low");
        realHighSeriesRelative = new XYSeries("Real High");
        realLowSeriesRelative = new XYSeries("Real Low");
        xySeriesCollection.addSeries(idealHighSeriesRelative);
        xySeriesCollection.addSeries(idealLowSeriesRelative);
        xySeriesCollection.addSeries(realHighSeriesRelative);
        xySeriesCollection.addSeries(realLowSeriesRelative);
        // Create chartAbsolute
        chartRelative = ChartFactory.createXYLineChart("Price change %", "Iteration", "Change %", xySeriesCollection, PlotOrientation.VERTICAL, true, true, true);
        initChart(chartRelative);

        //plot.getDomainAxis().setRange(1, (double)NeuralContext.Test.getMaxIterationCount());
    }
    /**
     * Initialization, common for both charts
     * @param chart 
     */
    private void initChart(JFreeChart chart){
   XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRangeAxis().setAutoRange(true);
        // Auto range
        NumberAxis valueAxis = (NumberAxis) plot.getRangeAxis();
        valueAxis.setAutoRangeIncludesZero(false);
        // Set line colors
        plot.getRenderer().setSeriesPaint(0, Color.CYAN);
        plot.getRenderer().setSeriesPaint(1, Color.BLUE);
        plot.getRenderer().setSeriesPaint(2, Color.MAGENTA);
        plot.getRenderer().setSeriesPaint(3, Color.RED);       
    }
    /**
     * Init JFreeChart
     */
    private void createChartAbsolute() {
        // Create dataset and series
        TableXYDataset ds = new DefaultTableXYDataset();
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        idealHighSeriesAbsolute = new XYSeries("Ideal High");
        idealLowSeriesAbsolute = new XYSeries("Ideal Low");
        realHighSeriesAbsolute = new XYSeries("Real High");
        realLowSeriesAbsolute = new XYSeries("Real Low");
        xySeriesCollection.addSeries(idealHighSeriesAbsolute);
        xySeriesCollection.addSeries(idealLowSeriesAbsolute);
        xySeriesCollection.addSeries(realHighSeriesAbsolute);
        xySeriesCollection.addSeries(realLowSeriesAbsolute);
        // Create chartAbsolute
        chartAbsolute = ChartFactory.createXYLineChart("Price values", "Iteration", "Price", xySeriesCollection, PlotOrientation.VERTICAL, true, true, true);
        
        initChart(chartAbsolute);
    }

    /**
     * Neural network related initialization
     */
    public final void init() {
        // Max iterations listener
        NeuralContext.Test.addPropertyChangeListener(PropertyNames.MAX_ITERATION_COUNT, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Set charts x range
                XYPlot plot = (XYPlot) chartAbsolute.getPlot();
                plot.getDomainAxis().setRange(1, (double) NeuralContext.Test.getMaxIterationCount());
                plot = (XYPlot) chartRelative.getPlot();
                plot.getDomainAxis().setRange(1, (double) NeuralContext.Test.getMaxIterationCount());
                
            }
        });
        // Iteration listener
        NeuralContext.Test.addPropertyChangeListener(PropertyNames.ITERATION, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                iterationLabel.setText(String.format("Iteration %d of %d", NeuralContext.Test.getIteration(), NeuralContext.Test.getMaxIterationCount()));
                iterationProgressBar.setMaximum(NeuralContext.Test.getMaxIterationCount());
                iterationProgressBar.setValue(NeuralContext.Test.getIteration());
            }
        });
        // Output entity listener
        NeuralContext.Test.addPropertyChangeListener(PropertyNames.REAL_OUTPUT_ENTITY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Add values to the charat
                int iteration = NeuralContext.Test.getIteration();
                // Add values to absolute chart
                idealHighSeriesAbsolute.add(iteration, NeuralContext.Test.getIdealEntity().getAbsoluteHigh());
                idealLowSeriesAbsolute.add(iteration, NeuralContext.Test.getIdealEntity().getAbsoluteLow());
                realHighSeriesAbsolute.add(iteration, NeuralContext.Test.getRealEntity().getAbsoluteHigh());
                realLowSeriesAbsolute.add(iteration, NeuralContext.Test.getRealEntity().getAbsoluteLow());
                // Add values to relative chart
                idealHighSeriesRelative.add(iteration, NeuralContext.Test.getIdealEntity().getRelativeHigh()/0.01);
                idealLowSeriesRelative.add(iteration, NeuralContext.Test.getIdealEntity().getRelativeLow()/0.01);
                realHighSeriesRelative.add(iteration, NeuralContext.Test.getRealEntity().getRelativeHigh()/0.01);
                realLowSeriesRelative.add(iteration, NeuralContext.Test.getRealEntity().getRelativeLow()/0.01);
            }
        });
    }
    
    /**
     * Clear both charts from data
     */
    private void clearCharts(){
                this.idealHighSeriesAbsolute.clear();
        this.idealLowSeriesAbsolute.clear();
        this.realHighSeriesAbsolute.clear();
        this.realLowSeriesAbsolute.clear();
        
        this.idealHighSeriesRelative.clear();
        this.idealLowSeriesRelative.clear();
        this.realHighSeriesRelative.clear();
        this.realLowSeriesRelative.clear();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        iterationProgressBar = new javax.swing.JProgressBar();
        iterationLabel = new javax.swing.JLabel();
        chartsPanel = new javax.swing.JSplitPane();
        absoluteChartPanel = new ChartPanel(chartAbsolute);
        relativeChartPanel = new ChartPanel(chartRelative);

        startButton.setText("Start test");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        iterationProgressBar.setStringPainted(true);

        iterationLabel.setText("Iteration");

        chartsPanel.setDividerLocation(200);
        chartsPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout absoluteChartPanelLayout = new javax.swing.GroupLayout(absoluteChartPanel);
        absoluteChartPanel.setLayout(absoluteChartPanelLayout);
        absoluteChartPanelLayout.setHorizontalGroup(
            absoluteChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 949, Short.MAX_VALUE)
        );
        absoluteChartPanelLayout.setVerticalGroup(
            absoluteChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );

        chartsPanel.setTopComponent(absoluteChartPanel);

        javax.swing.GroupLayout relativeChartPanelLayout = new javax.swing.GroupLayout(relativeChartPanel);
        relativeChartPanel.setLayout(relativeChartPanelLayout);
        relativeChartPanelLayout.setHorizontalGroup(
            relativeChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 949, Short.MAX_VALUE)
        );
        relativeChartPanelLayout.setVerticalGroup(
            relativeChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        chartsPanel.setBottomComponent(relativeChartPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iterationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iterationProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(34, 34, 34))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(chartsPanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(iterationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iterationProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartsPanel))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Start testing
     *
     * @param evt
     */
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        final TestPanel form = this;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        clearCharts();
        // Run testNetwork in new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Run testNetwork
                    NeuralService.testNetwork();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TestPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TestPanel.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    form.setCursor(Cursor.getDefaultCursor());
                }
            }
        }).start();

    }//GEN-LAST:event_startButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel absoluteChartPanel;
    private javax.swing.JSplitPane chartsPanel;
    private javax.swing.JLabel iterationLabel;
    private javax.swing.JProgressBar iterationProgressBar;
    private javax.swing.JPanel relativeChartPanel;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
