package bizz;

/**
 * Interface permettant la création d'objets
 * @author BOUREZ Philippe, LINS Sébastien, REYNERS Gaetan, STREEL Xavier
 * @version 1.1
 */
public interface BizzFactory {

	BizzFactory INSTANCE = BizzFactoryImpl.getInstance();

	/**
	 * Create an RSSI
	 * @param version the version
	 * @param from from who it was
	 * @param to who caught it
	 * @param rssi the RSSI value
     * @return
     */
	Rssi createRssi(int version, int from, int to, int rssi);

	Rssi createRssi();

}
