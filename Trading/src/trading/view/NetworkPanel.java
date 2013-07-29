/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trading.view;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.encog.neural.networks.BasicNetwork;
import trading.app.NeuralService;
import trading.common.NeuralContext;

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
        // Network property change
        NeuralContext.Network.addPropertyChangeListener("Network", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                BasicNetwork network = (BasicNetwork) evt.getNewValue();
                updateNetworkView(network);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createAndLearnButton = new javax.swing.JButton();
        networkLayersLabel = new javax.swing.JLabel();
        neuronsLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        createAndLearnButton.setText("Create&Learn");
        createAndLearnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAndLearnButtonActionPerformed(evt);
            }
        });

        networkLayersLabel.setText("Layers:");

        neuronsLabel.setText("Neurons: ");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Neural network summary");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 265, Short.MAX_VALUE)
                        .addComponent(createAndLearnButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(networkLayersLabel)
                            .addComponent(neuronsLabel)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createAndLearnButton)
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(networkLayersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neuronsLabel)
                .addContainerGap(188, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void createAndLearnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAndLearnButtonActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Create new network
        NeuralService.createNetwork();
        setCursor(Cursor.getDefaultCursor());
 
        // Train network in separate process
        //final NetworkPanel panel = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
               try {
                    //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // Train
                    NeuralService.trainNetwork();
                    //panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(NetworkPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NetworkPanel.class.getName()).log(Level.SEVERE, null, ex);
                }          }
        }).start();
    
        //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }//GEN-LAST:event_createAndLearnButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createAndLearnButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel networkLayersLabel;
    private javax.swing.JLabel neuronsLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * Display network info in this panel
     *
     * @param network
     */
    private void updateNetworkView(BasicNetwork network) {
        // Set first part of the texts
        networkLayersLabel.setText("Network layers: ");
        neuronsLabel.setText("Neurons: ");
        if (network == null) {
            return;
        }

        // Set layers label
        networkLayersLabel.setText(networkLayersLabel.getText() + network.getLayerCount());

        // Neurons in layers string like 13, 24, 34...
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < network.getLayerCount(); i++) {
            // Add neurons count to the string
            sb.append(network.getLayerNeuronCount(i));
            if (i < network.getLayerCount() - 1) {
                sb.append(",");
            }
        }
        // Set neurons label
        neuronsLabel.setText(neuronsLabel.getText() + sb.toString());
    }
}