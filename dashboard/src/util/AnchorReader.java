package util;

import app.AppContext;
import bizz.BizzFactory;
import model.AnchorModel;

/**
 * Created by jvdur on 31/05/2016.
 */
public class AnchorReader {

    public static void loadAnchorsToModel(AnchorModel am) {

        // CREATE GATEWAY
        int idGW = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor0Id"));
        am.addAnchor(BizzFactory.INSTANCE.createAnchor(idGW,0,0));

        // CREATE ANCHORS
        int nbAnchore = Integer.parseInt(AppContext.INSTANCE.getProperty("nbAnchors"));
        for (int i=1; i<=nbAnchore ; i++) {
            int id = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"Id"));
            int posx = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"x"));
            int posy = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"y"));
            am.addAnchor(BizzFactory.INSTANCE.createAnchor(id, posx, posy));
        }

    }

}
