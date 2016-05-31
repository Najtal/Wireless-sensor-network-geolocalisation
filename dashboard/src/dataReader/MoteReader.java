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

        Thread t = new Thread()
        {
            public void run() {

                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.logInfo("Mockup RSSI START 1");

                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(0,6,-14,1, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(1,6,-20,1, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(2,6,-14,1, RssiType.ANCHOR_TO_BLIND));

                try {
                    this.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.logInfo("Mockup RSSI START 2");

                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(3,6,-14,2, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(4,6,-20,2, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(2,6,-14,2, RssiType.ANCHOR_TO_BLIND));

                try {
                    this.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.logInfo("Mockup RSSI START 3");

                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(1,6,-14,3, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(3,6,-20,3, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(5,6,-14,3, RssiType.ANCHOR_TO_BLIND));


                try {
                    this.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.logInfo("Mockup RSSI START 3");

                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(0,6,-14,4, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(1,6,-20,4, RssiType.ANCHOR_TO_BLIND));
                RawModel.INSTANCE.addRssi(BizzFactory.INSTANCE.createRssi(2,6,-14,4, RssiType.ANCHOR_TO_BLIND));

            }
        };

        t.start();




    }


}
