/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dev;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.Executors;
import org.encog.util.simple.EncogUtility;

/**
 *
 * @author pdg
 */
public  class JavaApplication1 {
    public static double[] data = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame1().setVisible(true);
            }
        });
            
   }

    

}
