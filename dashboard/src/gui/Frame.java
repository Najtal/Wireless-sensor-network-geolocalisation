package gui;

import app.AppContext;
import model.AnalyzeModel;
import model.AnchorModel;
import util.Log;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * Created by jvdur on 09/05/2016.
 */
public class Frame extends JFrame implements ChangeListener, ActionListener {

    private Frame frame;

    // CONTAINERS
    // Side option pane
    private JPanel sideOptionPane;
    private JPanel menuPane;
    private JTable anchorPane;

    // South console
    private JPanel southConsole;

    // Visuallization
    private JScrollPane visualisationPane;
    private JPanel canvas;

    // DATA MODEL
    private final GuiModel model;



    /**
     * Main constructer
     */
    public Frame(GuiModel model) {

        this.model = model;
        this.frame = this;

        initFrame();

        initComponentStructure();

        AnalyzeModel.INSTANCE.setALGui(this);
        Log.registerLog(this);

    }



    /**
     * Init all settings for main frame
     */
    private void initFrame() {
        this.setTitle(AppContext.INSTANCE.getProperty("guiTitle"));
        this.setSize(
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeWidth")),
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeHeight")));
        this.setMinimumSize(new Dimension(
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeMinimumWidth")),
                Integer.parseInt(AppContext.INSTANCE.getProperty("guiSizeMinimumHeight"))));

        /* TODO add icon image
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        this.setIconImage(icon.getImage());
        */
        this.setLocationRelativeTo(null);

        // Close operations
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AppContext.INSTANCE.closeApplication();
            }
        });
    }


    private void initComponentStructure() {

        /*
            RootPane
                > MainContainer : center
                    > OptionPane : EAST
                    > VisualisationPane (CANVAS) : CENTER
                    > CliPane : SOUTH
        */

        // Init sideOption panel
        initSideOptionPane();
        // Init south option panel
        initSouthConsole();
        // Init canvas
        initVisualizationPane();

        // Set Vertical splitPane between canvas & option pane
        JSplitPane jspH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, visualisationPane, sideOptionPane);
        jspH.setResizeWeight(1.0);

        // Set Horizontal splitPane btw top pane & console pane
        JSplitPane jspV = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jspH, southConsole);
        //jspH.setDividerLocation(0.8);
        jspV.setResizeWeight(0.7);

        // Set listener to update canvas when JSplitPane are changed
        jspV.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                pce -> {
                    frame.validate();
                    frame.repaint();
                    frame.revalidate();
                });
        jspH.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                pce -> {
                    frame.validate();
                    frame.repaint();
                    frame.revalidate();
                });

        this.add(jspV);
    }


    private void initSideOptionPane() {

        // Init panes
        sideOptionPane = new JPanel(new BorderLayout());
        menuPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,10));

        anchorPane = new JTable(model.getTableModel());
        JScrollPane jscTable = new JScrollPane(anchorPane);

        // Init fields
        JTextField jbOpAddAnchor = new JTextField("Add anchor");
        JTextField jbOpSetGateway = new JTextField("Set gateway");
        JTextField jbOpConfig = new JTextField("Configure");
        JTextField jbOpStartStop = new JTextField("Start");

        // Add fields
        menuPane.add(jbOpAddAnchor);
        menuPane.add(jbOpSetGateway);
        menuPane.add(jbOpConfig);
        menuPane.add(jbOpStartStop);

        // Compose Panes
        sideOptionPane.add(jscTable, BorderLayout.CENTER);
        sideOptionPane.add(menuPane, BorderLayout.SOUTH);
    }

    private void initSouthConsole() {
        // Init panes
        southConsole = new JPanel(new BorderLayout());
        southConsole.setPreferredSize(new Dimension(200, 300));

        // Init fields
        JTextPane jtxConsole = new JTextPane();
        jtxConsole.setAutoscrolls(true);
        jtxConsole.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jtxConsole.setEditable(false);

        jtxConsole.setDocument(Log.getLogs());

        // Add fields
        JScrollPane jspConsole = new JScrollPane(jtxConsole);
        southConsole.add(jspConsole, BorderLayout.CENTER);
    }

    private void initVisualizationPane() {

        canvas = new Canvas(model);
        visualisationPane = new JScrollPane(canvas);
        visualisationPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        visualisationPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        visualisationPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                frame.validate();
                frame.repaint();
                frame.revalidate();
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
