package dataReader;

import app.AppContext;
import bizz.BizzFactory;
import bizz.Rssi;
import constant.RssiType;
import model.AnalyzeModel;
import model.AnchorModel;
import model.RawModel;
import ucc.AnchorDTO;
import ucc.AnchorUCC;
import ucc.RssiDTO;
import util.Log;

/**
 * Created by jvdur on 10/05/2016.
 */
public class CliParser {

    private static final int OFFSET = Integer.parseInt(AppContext.INSTANCE.getProperty("offset"));
    private static final boolean USE_BA_RSSI = Boolean.parseBoolean(AppContext.INSTANCE.getProperty("useBArssi"));
    private static final boolean USE_AB_RSSI = Boolean.parseBoolean(AppContext.INSTANCE.getProperty("useABrssi"));

    public static String[] parseIncomingLine(String line) {

        /* Split line into components */
        return line.replace("\n", "").replace("\r", "").split(",");
    }


    public static RssiDTO handleData(String[] data) {

        switch (data[1].split(":")[1]) {

            case "DATA_AA" : // Measurement anchor to anchor

                // Compute RSSI to Distance
                AnchorDTO aA = AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[2].split(":")[1]));
                AnchorDTO aB = AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[3].split(":")[1]));

                if (aA != null && aB != null) {
                    /*
                    Log.logSevere("RSSI: " + Integer.parseInt(data[4].split(":")[1]) + " -> " + Math.abs(Integer.parseInt(data[4].split(":")[1])+OFFSET));
                    Log.logSevere("ANCHOR1 X:" + AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[2].split(":")[1])).getPosx()
                            + " Y:" + AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[2].split(":")[1])).getPosy());
                    Log.logSevere("ANCHOR2 X:" + AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[3].split(":")[1])).getPosx()
                            + " Y:" + AnchorModel.INSTANCE.getAnchorById(Integer.parseInt(data[3].split(":")[1])).getPosy());
                    Log.logSevere("DISTANCE : " + AnchorUCC.getDistanceBtwAnchors(aA, aB));
                    */
                    double newRssiAt1m = AnchorUCC.getRssiAt1mFromAnchors(
                            Integer.parseInt(data[4].split(":")[1])+OFFSET, aA, aB);

                    // Log.logSevere("new RSSI : " + newRssiAt1m);

                    AnalyzeModel.INSTANCE.addAARssiAt1m((newRssiAt1m));
                }

                /*
                RssiDTO rssiAtA = BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_ANCHOR);
                RawModel.INSTANCE.addRssi(rssiAtA);
                */
                return null;

            case "DATA_AB" : // Measurement anchor to blind
                if (!USE_AB_RSSI) return null;
                return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1])+OFFSET,
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_BLIND);

            case "DATA_BA" : // Measurement blind to anchor
                if (!USE_BA_RSSI) return null;
                return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1])+OFFSET,
                        Integer.parseInt(data[6].split(":")[1]),
                        RssiType.ANCHOR_TO_BLIND);

                /*return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1])+OFFSET,
                        Integer.parseInt(data[6].split(":")[1]),
                        RssiType.BLIND_TO_ANCHOR);*/

            case "SLEEP" :
                break;

            case "FREQ" :
                break;

            case "BOOT" :
                break;

        }

        return null;

    }
}
