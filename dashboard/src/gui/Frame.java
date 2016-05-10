package gui;

import app.AppContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by jvdur on 09/05/2016.
 */
public class Frame extends JFrame implements ChangeListener {



    public Frame() {

        this.setTitle(AppContext.INSTANCE.getProperty("guiTitle"));
        this.setSize(
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeWidth")),
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeHeight")));
        this.setMinimumSize(new Dimension(
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeMinimumWidth")),
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeMinimumHeight"))));
        //ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        //this.setIconImage(icon.getImage());
        this.setLocationRelativeTo(null);

        // Close operations
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AppContext.INSTANCE.closeApplication();
            }
        });


        // TODO fill all the containers and make all the gui

        this.setVisible(true);
    }



    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

}
