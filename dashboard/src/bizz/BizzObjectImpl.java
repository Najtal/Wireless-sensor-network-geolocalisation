package bizz;

/**
 */
public abstract class BizzObjectImpl implements BizzObject {
	
	protected int version;

	/**
	 * BizzObject constructor
	 */
	public BizzObjectImpl() {
		this.version = 1;
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
	public void increaseVersion() {
		this.version++;
	}

}
