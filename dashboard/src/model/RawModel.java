package model;


import dataAnalyzer.LinearLeastSquareHandler;
import ucc.RssiDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jvdur on 09/05/2016.
 */
public class RawModel {

    public static final RawModel INSTANCE = new RawModel();

    private Map<Integer, RawModelSequence> sequences;
    private int lastSequenceAnalyzed;

    /**
     * construuctor
     */
    public RawModel() {
        sequences = new HashMap<Integer, RawModelSequence>();
        lastSequenceAnalyzed = -1;
    }

    /**
     * Add a Rssi to the model
     * @param rssi the RssiDTO to add to the model
     */
    public void addRssi(RssiDTO rssi) {

        int seqNumber = rssi.getSequenceNo();

        // If sequence not exist : we create, or if already past, we throw
        if (!sequences.containsKey(seqNumber) && seqNumber > lastSequenceAnalyzed) {
                sequences.put(seqNumber, new RawModelSequence(seqNumber));
        } else if (seqNumber < lastSequenceAnalyzed) {
            // if seq number already analyzed, we throw
            return;
        }

        sequences.get(seqNumber).addRssi(rssi);

        if (seqNumber > lastSequenceAnalyzed +1) {
            LinearLeastSquareHandler.INSTANCE.analyzeSequenceRawData(
                    sequences.get(lastSequenceAnalyzed+1), this,
                    AnalyzeModel.INSTANCE,
                    AnchorModel.INSTANCE,
                    lastSequenceAnalyzed+1);

            lastSequenceAnalyzed++;

        }
    }


    public void removeSequence(int sequenceNumber) {
        sequences.remove(sequenceNumber);
    }
}
