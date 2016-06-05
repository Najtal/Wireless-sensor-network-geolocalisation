package ucc;

import app.AppContext;

/**
 * Created by jvdur on 05/06/2016.
 */
public interface AnchorUCC {

    RssiUCC INSTANCE = RssiUCCImpl.getInstance();

    double PROPAGATION_CST_OF_PATHLOSS_EXP = Double.parseDouble(AppContext.INSTANCE.getProperty("propagationCstOfPathLossExp"));

    static double getRssiAt1mFromAnchors(int rssi, AnchorDTO anchor1, AnchorDTO anchor2) {
        double distance = getDistanceBtwAnchors(anchor1, anchor2);
        return (rssi - ( 10*PROPAGATION_CST_OF_PATHLOSS_EXP * Math.log10(distance) ));
    }

    static double getDistanceBtwAnchors(AnchorDTO aA, AnchorDTO aB) {
        int diffX = Math.max(aA.getPosx()-aB.getPosx() , aB.getPosx()-aA.getPosx());
        int diffY = Math.max(aA.getPosy()-aB.getPosy() , aB.getPosy()-aA.getPosy());
        return Math.sqrt(Math.pow(diffX, 2)+Math.pow(diffY, 2));
    }

}