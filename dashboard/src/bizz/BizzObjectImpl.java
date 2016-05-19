package bizz;

/**
 */
public abstract class BizzObjectImpl implements BizzObject {
	
	private int version;

	/**
	 * BizzObject constructor
	 * @param version		version number
	 */
	public BizzObjectImpl(final int version) {
		this.version = version;
	}
		
	/**
	 * empty constructor
	 */
	public BizzObjectImpl() {
		super();
	}


	/**
	 * get object version
	 * @return le numéro de version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * For every modification on the object increase the version
	 */
	protected void increaseVersion() {
		this.version++;
	}

}
