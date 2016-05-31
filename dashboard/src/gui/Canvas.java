package gui;

import model.AnalyzeModel;
import ucc.AnchorDTO;
import util.PositionDouble;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jvdur on 31/05/2016.
 */
public class Canvas extends JPanel {

    private final double zoom;
    private final int printAnShift;
    private final int printBlindShift;
    private GuiModel gm;
    private int originPosX;
    private int originPosY;

    private int printGwShift;

    /**
     * Consttructor for canvas
     * @param gm the GuiModel that host all data related to the GUI
     */
    public Canvas (GuiModel gm) {
        this.gm = gm;
        setBackground (Color.WHITE);
        setPreferredSize(new Dimension(gm.getEnvWidth(), gm.getEnvHeight()));
        zoom = gm.getEnvZoom();

        // Compution spacial positions (for optimization purposes
        this.originPosX = (int) ((gm.getEnvWidth()-gm.getEnvMaxX()) * zoom);
        this.originPosY = (int) (gm.getEnvMaxY() * zoom);
        this.printGwShift = ((int)(gm.getGwSize()*zoom/2));
        this.printAnShift = ((int)(gm.getAnchorSize()*zoom/2));
        this.printBlindShift = ((int)(gm.getBnSize()*zoom/2));

    }

    public void paintComponent (Graphics g) {

        Graphics2D g2;
        g2 = (Graphics2D) g;


        // Draw Background
        g.setColor(Color.white);
        g.fillRect(0,0,(int) (gm.getEnvWidth()*zoom), (int)(gm.getEnvHeight()*zoom));

        // Draw Grid
        int grid = gm.getGuiGrid();
        if (grid > 0) {
            g.setColor(new Color(240,240,240));

            int gridShift = (int)(grid*zoom);

            // Vertical left of GW
            for(int i=originPosX%grid ; i>=0 ; i-- ) {
                g.drawLine(originPosX-(i*gridShift), 0, originPosX-(i*gridShift), (int)(gm.getEnvHeight()*zoom));
            }

            // Vertical right of GW
            for(int i=0 ; i<(gm.getEnvWidth()%grid)-1 ; i++ ) {
                g.drawLine(originPosX+(i*gridShift), 0, originPosX+(i*gridShift), (int)(gm.getEnvHeight()*zoom));
            }

            // Horizontal top of GW
            for(int i=originPosY%grid ; i>=0 ; i-- ) {
                g.drawLine(0, originPosY-(i*gridShift), (int)(gm.getEnvWidth()*zoom), originPosY-(i*gridShift));
            }

            // Horizontal bottom of GW
            for(int i=0 ; i<(gm.getEnvHeight()%grid)-1 ; i++ ) {
                g.drawLine(0, originPosY+(i*gridShift), (int)(gm.getEnvWidth()*zoom), originPosY+(i*gridShift));
            }
        }

        // Draw the Anchors
        g.setColor(gm.getAnchorColor());
        for (AnchorDTO anchor : gm.getAm().getAnchorBy()) {

            if (anchor.getPosx() == 0 && anchor.getPosy() == 0) {
                g.setColor(gm.getAnchorColor());
                g.fillRect(originPosX-printGwShift, originPosY-printGwShift, ((int)(gm.getGwSize()*zoom)),((int)(gm.getGwSize()*zoom)));
            } else {
                g.setColor(gm.getGwColor());
                g.fillOval(originPosX+((int)(anchor.getPosx()*zoom))-printAnShift, originPosY+((int)((-1*anchor.getPosy())*zoom))-printAnShift, ((int) (gm.getAnchorSize()*zoom)), ((int) (gm.getAnchorSize()*zoom)));
            }

            g.setColor(Color.lightGray);
            g.drawOval(originPosX+(int)((anchor.getPosx()*zoom)-(gm.getMoteRadius()*zoom)), originPosY+(int)(((-1*anchor.getPosy())*zoom)-(gm.getMoteRadius()*zoom)), (int)(gm.getMoteRadius()*2*zoom), (int)(gm.getMoteRadius()*2*zoom));

        }


        // Paint moving mote
        g.setColor(gm.getBnColor());

        PositionDouble bpd = AnalyzeModel.INSTANCE.getLastPosition();
        if (bpd != null) {
            g.setColor(gm.getBnColor());
            g.fillOval(originPosX+((int)(bpd.getX()*zoom))-printBlindShift, originPosY+((int)((-1*bpd.getY())*zoom))-printBlindShift, ((int) (gm.getBnSize()*zoom)), ((int) (gm.getBnSize()*zoom)));
            g.drawOval(originPosX+(int)((bpd.getX()*zoom)-(gm.getMoteRadius()*zoom)), originPosY+(int)(((-1*bpd.getY())*zoom)-(gm.getMoteRadius()*zoom)), (int)(gm.getMoteRadius()*2*zoom), (int)(gm.getMoteRadius()*2*zoom));

        }

    }

}
