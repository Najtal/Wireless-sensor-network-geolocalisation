package model;

import util.Position;
import util.PositionDouble;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jvdur on 09/05/2016.
 */
public class AnalyzeModel {

    public final  static AnalyzeModel INSTANCE = new AnalyzeModel();

    private List<PositionDouble> blindPositions;
    private Map<Integer, PositionDouble> positionsMap;

    private ActionListener alGui;

    /**
     * constructor
     */
    private AnalyzeModel() {
        this.blindPositions = new ArrayList<>();
        this.positionsMap = new HashMap<>();
    }

    public void addBlindPosition(PositionDouble blindPosition, int sequenceNumber) {
        blindPositions.add(blindPosition);
        positionsMap.put(sequenceNumber, blindPosition);
        triggerGui();
    }

    public PositionDouble getLastPosition() {
        if (blindPositions.size() > 0)
            return blindPositions.get(blindPositions.size()-1);
        return null;
    }

    public void setALGui(ActionListener alGui) {
        this.alGui = alGui;
    }

    private void triggerGui() {
        if(alGui != null)
            alGui.actionPerformed(null);
    }
}
