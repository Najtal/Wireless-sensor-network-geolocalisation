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
        double offsetGW = Double.parseDouble(AppContext.INSTANCE.getProperty("anchor0Offset"));
        double alphaGW  = Double.parseDouble(AppContext.INSTANCE.getProperty("anchor0Alpha"));
        am.addAnchor(BizzFactory.INSTANCE.createAnchor(idGW,0,0,offsetGW,alphaGW));

        // CREATE ANCHORS
        int nbAnchore = Integer.parseInt(AppContext.INSTANCE.getProperty("nbAnchors"));
        for (int i=1; i<=nbAnchore ; i++) {
            int id = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"Id"));
            int posx = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"x"));
            int posy = Integer.parseInt(AppContext.INSTANCE.getProperty("anchor"+i+"y"));
            double offset = Double.parseDouble(AppContext.INSTANCE.getProperty("anchor"+i+"Offset"));
            double alpha = Double.parseDouble(AppContext.INSTANCE.getProperty("anchor"+i+"Alpha"));
            am.addAnchor(BizzFactory.INSTANCE.createAnchor(id, posx, posy, offset, alpha));
        }

    }

}
