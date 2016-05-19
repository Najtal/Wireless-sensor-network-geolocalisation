package model;

import constant.RssiType;
import ucc.RssiDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvdur on 19/05/2016.
 */
public class RawModelSequence {


    private int sequenceNo;
    private List<RssiDTO> rssiAtoA;
    private List<RssiDTO> rssiAtoB;
    private List<RssiDTO> rssiBtoA;

    private RawModelSequence() {
    }

    protected RawModelSequence(int sequenceNo) {
        this.sequenceNo = sequenceNo;
        rssiAtoB = new ArrayList<>();
        rssiAtoA = new ArrayList<>();
        rssiBtoA = new ArrayList<>();
    }

    /*
     * add new RSSI to the model.
     */
    public void addRssi(RssiDTO rssi) {

        if (rssi.getType() == RssiType.ANCHOR_TO_BLIND) {
            rssiAtoB.add(rssi);
        } else if (rssi.getType() == RssiType.ANCHOR_TO_ANCHOR) {
            rssiAtoA.add(rssi);
        } else {
            rssiBtoA.add(rssi);
        }
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public List<RssiDTO> getRssiAtoA() {
        return rssiAtoA;
    }

    public List<RssiDTO> getRssiAtoB() {
        return rssiAtoB;
    }

    public List<RssiDTO> getRssiBtoA() {
        return rssiBtoA;
    }
}
