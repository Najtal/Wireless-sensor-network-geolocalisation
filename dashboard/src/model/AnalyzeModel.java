package model;

import app.AppContext;
import util.PositionDouble;

import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by jvdur on 09/05/2016.
 */
public class AnalyzeModel {

    public final  static AnalyzeModel INSTANCE = new AnalyzeModel();

    private final static int AVG_POSITIONS = Integer.parseInt(AppContext.INSTANCE.getProperty("avgPosition"));

    private Map<Long, PositionDouble> positionsMap;
    private ArrayList<Long> regTimePositions;
    private PositionDouble lastPosition;

    private ActionListener alGui;

    /**
     * constructor
     */
    private AnalyzeModel() {
        this.positionsMap = new HashMap<>();
        this.regTimePositions = new ArrayList<>();
    }

    public synchronized void addBlindPosition(PositionDouble blindPosition, int sequenceNumber) {
        Long nanoTime = System.nanoTime();
        positionsMap.put(nanoTime, blindPosition);
        regTimePositions.add(nanoTime);
        Collections.sort(regTimePositions);
        lastPosition = blindPosition;
        triggerGui();
    }

    public PositionDouble getLastPosition() {
        return lastPosition;
    }

    public void setALGui(ActionListener alGui) {
        this.alGui = alGui;
    }

    private void triggerGui() {
        if(alGui != null)
            alGui.actionPerformed(null);
    }

    public Map<Long, PositionDouble> getPositionsMap() {
        return positionsMap;
    }

    public ArrayList<Long> getRegTimePositions() {
        return regTimePositions;
    }

    public synchronized PositionDouble getAvgPosition() {
        double avgPosX = 0, avgPosY = 0;
        int nbPos = regTimePositions.size();

        for (int i=Math.max(nbPos-AVG_POSITIONS,0) ; i<nbPos ; i++) {
            avgPosX += positionsMap.get(regTimePositions.get(i)).getX();
            avgPosY += positionsMap.get(regTimePositions.get(i)).getY();
        }

        int devider = Math.min(nbPos, AVG_POSITIONS);
        return  new PositionDouble(avgPosX / devider, avgPosY / devider);
    }
}
