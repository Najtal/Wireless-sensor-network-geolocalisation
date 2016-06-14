package model;


import dataAnalyzer.LinearLeastSquareHandler;
import ucc.RssiDTO;
import util.Log;

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

        Log.logInfo("new RSSI : seqno=" + seqNumber + ", from="+rssi.getFrom()+ ", to="+rssi.getTo()+", rssi="+rssi.getRssi() + ", distance=" + rssi.getDistanceMeters());

        // If sequence not exist : we create, or if already past, we throw
        if (!sequences.containsKey(seqNumber) ) {  //&& seqNumber > lastSequenceAnalyzed) {
                sequences.put(seqNumber, new RawModelSequence(seqNumber));

                new LinearLeastSquareHandler().analyzeSequenceRawData(
                        sequences.get(seqNumber),
                        this,
                        AnalyzeModel.INSTANCE,
                        AnchorModel.INSTANCE,
                        seqNumber);
                lastSequenceAnalyzed++;
        }

        sequences.get(seqNumber).addRssi(rssi);
    }


    public synchronized void removeSequence(int sequenceNumber) {
        sequences.remove(sequenceNumber);
    }
}
