package dataReader;

import app.AppContext;
import bizz.BizzFactory;
import bizz.Rssi;
import constant.RssiType;
import model.RawModel;
import ucc.RssiDTO;
import util.Log;

/**
 * Created by jvdur on 10/05/2016.
 */
public class CliParser {

    public static String[] parseIncomingLine(String line) {

        Log.logInfo("GW_MOTE_DATA : " + line);

        if (line == null) {
            System.err.println("Parsing null line");
            return null;
        }

        /* Split line into components */
        return line.split(" ");
    }


    public static void handleData(String[] data, RawModel rawModel) {



        switch (data[1].split(":")[1]) {

            // TODO : Parse receiving string, create the appropriate BO, and send it to the rawModel

            case "DATA_AA" : // Measurement anchor to anchor
                RssiDTO rssiAtA = BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_ANCHOR);

                RawModel.INSTANCE.addRssi(rssiAtA);
                break;

            case "DATA_AB" : // Measurement anchor to blind
                RssiDTO rssiAtoB = BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_BLIND);

                RawModel.INSTANCE.addRssi(rssiAtoB);
                break;

            case "DATA_BA" : // Measurement blind to anchor
                RssiDTO rssiBtA = BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.BLIND_TO_ANCHOR);

                RawModel.INSTANCE.addRssi(rssiBtA);
                break;

            case "SLEEP" :

                break;

            case "FREQ" :

                break;

            case "BOOT" :

                break;

        }


    }
}
