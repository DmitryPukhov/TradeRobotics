/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.view;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import java.io.FileNotFoundException;
import java.lang.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.SwingUtilities;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.AnalyzeNetwork;
import trading.app.NeuralService;
import trading.common.NeuralContext;
import trading.common.PropertyNames;

/**
 *
 * @author pdg
 */
public class NetworkPanel extends javax.swing.JPanel {

    /**
     * Creates new form NetworkPanelf
     */
    public NetworkPanel() {
        initComponents();

        init();
    }

    /**
     * Neural network specific init
     */
    public final void init() {
        neuronsInLayersText.setText(String.format("%s,%s,%s", NeuralContext.NetworkSettings.getInputSize(), NeuralContext.NetworkSettings.getHidden1Count(), NeuralContext.NetworkSettings.getOutputSize()));
        
        // Network property change
        NeuralContext.Network.addPropertyChangeListener(PropertyNames.NETWORK, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                BasicNetwork network = (BasicNetwork) evt.getNewValue();
                updateNetworkView(network);
            }
        });
        NeuralContext.Network.addPropertyChangeListener(PropertyNames.EPOCH, new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateNetworkView(NeuralContext.Network.getNetwork());
            }
        });
    }

    
    /**
     * Create and init file chooser
     *
     * @return
     */
    public final JFileChooser getFileChooser() {
        JFileChooser chooser = new JFileChooser();
        // Select files, not directories
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File("network"));
        // Set file filter
        chooser.setFileFilter(new FileFilter(){
            private static final String extension = ".network";
            @Override
            public boolean accept(File f) {
                return true;
                //return f.getName().endsWith(extension);
            }

            @Override
            public String getDescription() {
                return extension;
            }
        });

        return chooser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        networkLayersLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        createButton = new javax.swing.JButton();
        neuronsInLayersText = new javax.swing.JTextField();
        weightsLabel = new javax.swing.JLabel();

        networkLayersLabel.setText("Layers:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Neural network summary");

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        loadButton.setText("Load");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        neuronsInLayersText.setText("0,0,0");
        neuronsInLayersText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                neuronsInLayersTextActionPerformed(evt);
            }
        });

        weightsLabel.setText("Total weigths:0, non zero: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(networkLayersLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(neuronsInLayersText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(weightsLabel))
                .addGap(90, 90, 90)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(loadButton)
                        .addGap(33, 33, 33)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetButton)
                        .addContainerGap(134, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(createButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(neuronsInLayersText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(networkLayersLabel))
                        .addGap(30, 30, 30)
                        .addComponent(weightsLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        List<Integer> layers = getNetworkLayers();
        NeuralService.createNetwork(layers);
    }//GEN-LAST:event_createButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        // Ask for path and load
        JFileChooser openFile = getFileChooser();
        if (openFile.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Load
            NeuralService.loadNetwork(openFile.getSelectedFile());
        }
//
    }//GEN-LAST:event_loadButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // Ask for path and save
        JFileChooser saveFile = getFileChooser();
        if(saveFile.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            NeuralService.saveNetwork(saveFile.getSelectedFile());
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        NeuralService.resetNetwork();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void neuronsInLayersTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_neuronsInLayersTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_neuronsInLayersTextActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton loadButton;
    private javax.swing.JLabel networkLayersLabel;
    private javax.swing.JTextField neuronsInLayersText;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel weightsLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Display network info in this panel
     *
     * @param network
     */
    private void updateNetworkView(BasicNetwork network) {
        // Display neurons in layers in text field
        updateNetworkLayersText(network);
        updateNetworkWeights(network);;

        

                
    }
    /**
     * Display neurons in layers count
     * @param network 
     */
    private void updateNetworkLayersText(BasicNetwork network){
        neuronsInLayersText.setText("0");
        if (network == null) {
            return;
        }      
         // Neurons in layers string like 13, 24, 34...
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < network.getLayerCount(); i++) {
            // Add neurons count to the string
            sb.append(network.getLayerNeuronCount(i));
            if (i < network.getLayerCount() - 1) {
                sb.append(",");
            }
            // Add neurons to text area
         }  
        neuronsInLayersText.setText(sb.toString());
        
    }
    
    /**
     * Update network configuration text with current neurons in layers
     * @param network
     * @return 
     */
    private void updateNetworkWeights(BasicNetwork network){
        final String testTemplate = "Weights total:%d, non zero: %d";
        weightsLabel.setText(String.format(testTemplate, 0,0));
        
        // Calculate non zero weights
        AnalyzeNetwork analyze = new AnalyzeNetwork(network);
        int totalWeights = analyze.getWeightValues().length;
        int nonZeroWeights = 0;
        for(double weight: analyze.getWeightValues()){
            if(weight< -0.00000000001 || weight > 0.00000000001){
                nonZeroWeights++;
            }
        }
        // Display
        weightsLabel.setText(String.format(testTemplate, totalWeights, nonZeroWeights));
        
    }
    
    /**
     * Get neurons in network
     * @return 
     */
    private List<Integer> getNetworkLayers(){
        // Fill values from string
        String[] splittedString = neuronsInLayersText.getText().split(",");
        List<Integer> neurons = new ArrayList<>();
        int i = 0;
        for(String stringVal : splittedString){
            int val = Integer.parseInt(stringVal);
            neurons.add(val);
        }
        return neurons;
    }
}
