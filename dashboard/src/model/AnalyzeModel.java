package model;

import util.Position;
import util.PositionDouble;

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

    /**
     * constructor
     */
    private AnalyzeModel() {

        this.blindPositions = new ArrayList<>();
        this.positionsMap = new HashMap<>();

        // TODO: 10/05/2016

    }

    public void addBlindPosition(PositionDouble blindPosition, int sequenceNumber) {
        blindPositions.add(blindPosition);
        positionsMap.put(sequenceNumber, blindPosition);
    }
}
