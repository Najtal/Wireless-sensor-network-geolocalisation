package ucc;

import app.AppContext;

/**
 */
public interface RssiUCC {
	
	RssiUCC INSTANCE = RssiUCCImpl.getInstance();

    int RECEIVED_RSSI_AT_1M = Integer.parseInt(AppContext.INSTANCE.getProperty("receivedRssiAt1m"));
    double PROPAGATION_CST_OF_PATHLOSS_EXP = Double.parseDouble(AppContext.INSTANCE.getProperty("propagationCstOfPathLossExp"));

    static double getDistanceFromRssi(int rssi) {

        double exp = ((double)rssi-(double)RECEIVED_RSSI_AT_1M) / (10*PROPAGATION_CST_OF_PATHLOSS_EXP);
        double d = Math.pow(10, exp );
        return d;

    }
}
