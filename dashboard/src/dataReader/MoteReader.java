package dataReader;

import app.AppContext;
import bizz.BizzFactory;
import constant.RssiType;
import exception.MoteReaderException;
import model.AnchorModel;
import model.RawModel;
import util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jvdur on 09/05/2016.
 */
public class MoteReader {

    private final RawModel rawModel;

    public MoteReader(RawModel rawModel) {

        // Init context variables
        this.rawModel = rawModel;

        if (Integer.parseInt(AppContext.INSTANCE.getProperty("mockupRSSI")) == 1) {
            mockupRSSI();
        } else {
            Reader readerThread = new Reader(rawModel);
            readerThread.start();

        }


    }

    private void mockupRSSI() {

        System.out.println("MOCKUP RSSI");

        AnchorModel.INSTANCE.addAnchor(BizzFactory.INSTANCE.createAnchor(0, 0, 0));
        AnchorModel.INSTANCE.addAnchor(BizzFactory.INSTANCE.createAnchor(1, 10, 0));
        AnchorModel.INSTANCE.addAnchor(BizzFactory.INSTANCE.createAnchor(2, 10, 10));

        RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(0,3,-14,1, RssiType.ANCHOR_TO_BLIND));
        RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(1,3,-20,1, RssiType.ANCHOR_TO_BLIND));
        RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(2,3,-14,1, RssiType.ANCHOR_TO_BLIND));


    }


}
