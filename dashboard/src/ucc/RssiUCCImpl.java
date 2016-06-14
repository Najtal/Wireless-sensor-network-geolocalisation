package ucc;

/**
 * Use case cntroller over RSSI
 */
public class RssiUCCImpl implements RssiUCC {

    static RssiUCCImpl instance;

    /**
     * To get the singleton instance of RssiUCCImpl
     * @return the instance singleton of RssiUCCImpl
     */
    public static RssiUCC getInstance() {
        if (instance == null)
            instance = new RssiUCCImpl();
        return instance;
    }

    /**
     * privae emmpty controller
     */
    private RssiUCCImpl() {
    }



}
