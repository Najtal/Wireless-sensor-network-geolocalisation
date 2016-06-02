package model;

import util.Position;
import util.PositionDouble;

import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by jvdur on 09/05/2016.
 */
public class AnalyzeModel {

    public final  static AnalyzeModel INSTANCE = new AnalyzeModel();

    private Map<Integer, PositionDouble> positionsMap;
    private ArrayList<Integer> regSequences;
    private int lastPositionIndex;

    private ActionListener alGui;

    /**
     * constructor
     */
    private AnalyzeModel() {
        this.positionsMap = new HashMap<>();
        this.regSequences = new ArrayList<>();
        this.lastPositionIndex = -1;
    }

    public void addBlindPosition(PositionDouble blindPosition, int sequenceNumber) {
        positionsMap.put(sequenceNumber, blindPosition);
        regSequences.add(sequenceNumber);
        Collections.sort(regSequences);
        if (sequenceNumber > lastPositionIndex || sequenceNumber < 20 && lastPositionIndex > 200) {
            lastPositionIndex = sequenceNumber;
        }
        triggerGui();
    }

    public PositionDouble getLastPosition() {
        if (lastPositionIndex >= 0)
            return positionsMap.get(lastPositionIndex);
        return null;
    }

    public void setALGui(ActionListener alGui) {
        this.alGui = alGui;
    }

    private void triggerGui() {
        if(alGui != null)
            alGui.actionPerformed(null);
    }

    public Map<Integer, PositionDouble> getPositionsMap() {
        return positionsMap;
    }

    public ArrayList<Integer> getRegSequences() {
        return regSequences;
    }
}
