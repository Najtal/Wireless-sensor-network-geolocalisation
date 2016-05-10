package bizz;

/**
 * Class generating bizz objects
 */
public class BizzFactoryImpl implements BizzFactory {

	static BizzFactoryImpl instance;

	/**
	 * To get the singleton instance of BizzFactoryImpl
	 * @return the instance singleton of BizzFactoryImpl
     */
	public static BizzFactory getInstance() {
		if (instance == null)
			instance = new BizzFactoryImpl();
		return instance;
	}

	/**
	 * private empty controller
	 */
	private BizzFactoryImpl() {
	}

	/*
	 * RSSI bizz objects
	 */
	@Override
	public Rssi createRssi(int version, int from, int to, int rssi) {
		return new RssiImpl(version, from, to, rssi);
	}

	@Override
	public Rssi createRssi() {
		return new RssiImpl();
	}


}
