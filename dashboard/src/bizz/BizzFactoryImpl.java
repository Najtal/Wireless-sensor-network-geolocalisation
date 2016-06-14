package bizz;

import constant.RssiType;

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
	public Rssi createRssi(int from, int to, int rssi, int sequenceNo, RssiType type) {
		return new RssiImpl(from, to, rssi, sequenceNo, type);
	}

	@Override
	public Rssi createRssi() {
		return new RssiImpl();
	}

	@Override
	public Anchors createAnchor(int id, int posx, int posy, double offset) {
		return new AnchorsImpl(id, posx, posy, offset);
	}

	@Override
	public Anchors createAnchor() {
		return new AnchorsImpl();
	}


}
