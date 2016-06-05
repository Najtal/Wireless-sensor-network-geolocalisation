package ucc;

import app.AppContext;
import model.AnalyzeModel;

/**
 */
public interface RssiUCC {
	
	RssiUCC INSTANCE = RssiUCCImpl.getInstance();

    int RECEIVED_RSSI_AT_1M = Integer.parseInt(AppContext.INSTANCE.getProperty("receivedRssiAt1m"));
    double PROPAGATION_CST_OF_PATHLOSS_EXP = Double.parseDouble(AppContext.INSTANCE.getProperty("propagationCstOfPathLossExp"));
    boolean USE_AVG_RSSI_AT_1M = Boolean.parseBoolean(AppContext.INSTANCE.getProperty("useAvgRssiAt1m"));

    static double getDistanceFromRssi(int rssi) {

        double rssiAt1m = RECEIVED_RSSI_AT_1M;

        if (USE_AVG_RSSI_AT_1M) {
            rssiAt1m = AnalyzeModel.INSTANCE.getLastRssiAt1m();
        }

        return Math.pow(10, (rssi-rssiAt1m) / (10*PROPAGATION_CST_OF_PATHLOSS_EXP) );
    }
}
