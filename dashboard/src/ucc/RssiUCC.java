package ucc;

/**
 */
public interface RssiUCC {
	
	RssiUCC INSTANCE = RssiUCCImpl.getInstance();

    static int getDistanceFromRssi(int rssi) {

        // TODO : convert a RSSI distance (-0 to -100) in a distance (meters)

        return Math.abs(rssi);

    }
}
