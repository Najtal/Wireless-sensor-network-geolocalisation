package gui;

import app.AppContext;
import model.AnalyzeModel;
import model.AnchorModel;

import java.awt.*;

/**
 * Created by jvdur on 31/05/2016.
 */
public class GuiModel {

    private AnchorModel am;
    private CustomTableModel tableModel;

    private int envMaxX = Integer.parseInt(AppContext.INSTANCE.getProperty("envMaxX"));
    private int envMaxY = Integer.parseInt(AppContext.INSTANCE.getProperty("envMaxY"));
    private int envHeight = Integer.parseInt(AppContext.INSTANCE.getProperty("envHeight"));
    private int envWidth = Integer.parseInt(AppContext.INSTANCE.getProperty("envWidth"));
    private double envZoom = Double.parseDouble(AppContext.INSTANCE.getProperty("envZoom"));
    private final int moteRadius = Integer.parseInt(AppContext.INSTANCE.getProperty("moteRadius"));

    private final int anchorSize = Integer.parseInt(AppContext.INSTANCE.getProperty("guiAnchorSize"));
    private final int anchorColorR = Integer.parseInt(AppContext.INSTANCE.getProperty("guiAnchorColorR"));
    private final int anchorColorG = Integer.parseInt(AppContext.INSTANCE.getProperty("guiAnchorColorG"));
    private final int anchorColorB = Integer.parseInt(AppContext.INSTANCE.getProperty("guiAnchorColorB"));
    private Color anchorColor;

    private final int gwSize = Integer.parseInt(AppContext.INSTANCE.getProperty("guiGwSize"));
    private final int gwColorR = Integer.parseInt(AppContext.INSTANCE.getProperty("guiGwColorR"));
    private final int gwColorG = Integer.parseInt(AppContext.INSTANCE.getProperty("guiGwColorG"));
    private final int gwColorB = Integer.parseInt(AppContext.INSTANCE.getProperty("guiGwColorB"));
    private Color gwColor;

    private final int bnSize = Integer.parseInt(AppContext.INSTANCE.getProperty("guiBnSize"));
    private final int bnColorR = Integer.parseInt(AppContext.INSTANCE.getProperty("guiBnColorR"));
    private final int bnColorG = Integer.parseInt(AppContext.INSTANCE.getProperty("guiBnColorG"));
    private final int bnColorB = Integer.parseInt(AppContext.INSTANCE.getProperty("guiBnColorB"));
    private Color bnColor;

    private int guiGrid = Integer.parseInt(AppContext.INSTANCE.getProperty("guiGrid"));
    private boolean showGrid;
    private boolean showRadius;
    private boolean showPath;
    private boolean showID;
    private boolean showAvg;

    public GuiModel(AnchorModel am) {
        this.am = am;
        this.tableModel = new CustomTableModel(am);

        this.anchorColor = new Color(anchorColorR, anchorColorG, anchorColorB);
        this.gwColor = new Color(gwColorR, gwColorG, gwColorB);
        this.bnColor = new Color(bnColorR, bnColorG, bnColorB);

        this.showGrid = (guiGrid == 0) ? false : true;
        this.showRadius = true;
        this.showPath = true;
        this.showID = true;
        this.showAvg = false;
    }

    public AnchorModel getAm() {
        return am;
    }

    public CustomTableModel getTableModel() {
        return tableModel;
    }

    public int getEnvMaxX() {
        return envMaxX;
    }

    public void setEnvMaxX(int envMaxX) {
        this.envMaxX = envMaxX;
    }

    public int getEnvMaxY() {
        return envMaxY;
    }

    public void setEnvMaxY(int envMaxY) {
        this.envMaxY = envMaxY;
    }

    public int getEnvHeight() {
        return envHeight;
    }

    public void setEnvHeight(int envHeight) {
        this.envHeight = envHeight;
    }

    public int getEnvWidth() {
        return envWidth;
    }

    public void setEnvWidth(int envWidth) {
        this.envWidth = envWidth;
    }

    public int getMoteRadius() {
        return moteRadius;
    }

    public int getAnchorSize() {
        return anchorSize;
    }

    public int getGwSize() {
        return gwSize;
    }

    public Color getAnchorColor() {
        return anchorColor;
    }

    public Color getGwColor() {
        return gwColor;
    }

    public double getEnvZoom() {
        return envZoom;
    }

    public Color getBnColor() {
        return bnColor;
    }

    public int getBnSize() {
        return bnSize;
    }

    public int getGuiGrid() {
        return guiGrid;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowRadius() {
        return showRadius;
    }

    public void setShowRadius(boolean showRadius) {
        this.showRadius = showRadius;
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public boolean isShowID() {
        return showID;
    }

    public void setShowID(boolean showID) {
        this.showID = showID;
    }


    public boolean isShowAvg() {
        return showAvg;
    }

    public void setShowAvg(boolean showAvg) {
        this.showAvg = showAvg;
    }
}
