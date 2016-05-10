package bizz;


/**
 */
public class RssiImpl extends BizzObjectImpl implements Rssi {

	private int from;
	private int to;
	private int rssi;

	/**
	 *
	 * @param version the version
	 * @param from which mote was the transmitter
	 * @param to which mote received it
     * @param rssi at what strength did the receiver mote get the message
     */
	public RssiImpl(final int version, final int from, final int to, final int rssi) {
		super(version);
		this.from = from;
		this.to = to;
		this.rssi = rssi;
	}

	/**
	 * Constructeur vide
	 */
	public RssiImpl() {
	}

	@Override
	public int getFrom() {
		return from;
	}

	@Override
	public void setFrom(int from) {
		this.from = from;
	}

	@Override
	public int getTo() {
		return to;
	}

	@Override
	public void setTo(int to) {
		this.to = to;
	}

	@Override
	public int getRssi() {
		return rssi;
	}

	@Override
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
}
