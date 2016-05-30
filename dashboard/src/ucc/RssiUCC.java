package ucc;

import app.AppContext;

/**
 */
public interface RssiUCC {
	
	RssiUCC INSTANCE = RssiUCCImpl.getInstance();

    public static final int RECEIVED_RSSI_AT_1M = Integer.parseInt(AppContext.INSTANCE.getProperty("receivedRssiAt1m"));
    public static final int PROPAGATION_CST_OF_PATHLOSS_EXP = Integer.parseInt(AppContext.INSTANCE.getProperty("propagationCstOfPathLossExp"));

    static double getDistanceFromRssi(int rssi) {

        double exp = ((double)rssi-(double)RECEIVED_RSSI_AT_1M) / (10*(double)PROPAGATION_CST_OF_PATHLOSS_EXP);
        double d = Math.pow(10, exp );
        return d;

    }
}
