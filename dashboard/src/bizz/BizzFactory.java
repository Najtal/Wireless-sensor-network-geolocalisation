package bizz;

import constant.RssiType;
import ucc.AnchorDTO;
import ucc.RssiDTO;

/**
 * Interface permettant la création d'objets
 * @author BOUREZ Philippe, LINS Sébastien, REYNERS Gaetan, STREEL Xavier
 * @version 1.1
 */
public interface BizzFactory {

	BizzFactory INSTANCE = BizzFactoryImpl.getInstance();

	/**
	 * Create an RSSI
	 * @param from from who it was
	 * @param to who caught it
	 * @param rssi the RSSI value
     * @return
     */
	RssiDTO createRssi(int from, int to, int rssi, int sequenceNo, RssiType type, double alpha);

	RssiDTO createRssi();

	/**
	 *
	 * @param id The id of the anchor
	 * @param posx It's X position
	 * @param posy It's Y position
     * @return
     */
	AnchorDTO createAnchor(int id, int posx, int posy, double offset, double alpha);

	AnchorDTO createAnchor();

}
