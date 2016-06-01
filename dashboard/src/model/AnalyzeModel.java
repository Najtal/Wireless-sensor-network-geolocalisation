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

    private ActionListener alGui;

    /**
     * constructor
     */
    private AnalyzeModel() {
        this.positionsMap = new HashMap<>();
        this.regSequences = new ArrayList<>();
    }

    public void addBlindPosition(PositionDouble blindPosition, int sequenceNumber) {
        positionsMap.put(sequenceNumber, blindPosition);
        regSequences.add(sequenceNumber);
        Collections.sort(regSequences);
        triggerGui();
    }

    public PositionDouble getLastPosition() {
        if (regSequences.size() > 0)
            return positionsMap.get(regSequences.size()-1);
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
