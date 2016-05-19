package model;


import ucc.RssiDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jvdur on 09/05/2016.
 */
public class RawModel {


    private static RawModel instance;

    private Map<Integer, RawModelSequence> sequences;
    private int lastSequenceAnalyzed;


    /**
     * Model singleton
     * @return
     */
    public static RawModel getInstance() {
        if (instance == null)
            instance = new RawModel();
        return instance;
    }

    /**
     * private cconstruuctor
     */
    private RawModel() {
        sequences = new HashMap<Integer, RawModelSequence>();
        lastSequenceAnalyzed = -1;
    }


    public void addRssi(RssiDTO rssi) {

        int seqNumber = rssi.getSequenceNo();

        // If sequence not exist : we create, or if already past, we throw
        if (!sequences.containsKey(seqNumber)) {
            if (seqNumber > lastSequenceAnalyzed) {
                sequences.put(seqNumber, new RawModelSequence(seqNumber));
            } else {
                return;
            }
        }

        sequences.get(seqNumber).addRssi(rssi);
    }


}
