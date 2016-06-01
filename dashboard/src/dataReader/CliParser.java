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

        /* Split line into components */
        return line.replace("\n", "").replace("\r", "").split(",");
    }


    public static RssiDTO handleData(String[] data, RawModel rawModel) {

        switch (data[1].split(":")[1]) {


            // TODO : handle the Anchor-to-anchor data
            /*
            case "DATA_AA" : // Measurement anchor to anchor
                RssiDTO rssiAtA = BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_ANCHOR);


                RawModel.INSTANCE.addRssi(rssiAtA);
                break;
            */

            /*
            case "DATA_AB" : // Measurement anchor to blind
                return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[5].split(":")[1]),
                        RssiType.ANCHOR_TO_BLIND);
            */
            // TODO : handle the Blinf-to-anchor data

            case "DATA_BA" : // Measurement blind to anchor
                /*return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1]),
                        Integer.parseInt(data[6].split(":")[1]),
                        RssiType.BLIND_TO_ANCHOR);*/
                return BizzFactory.INSTANCE.createRssi(
                        Integer.parseInt(data[3].split(":")[1]),
                        Integer.parseInt(data[2].split(":")[1]),
                        Integer.parseInt(data[4].split(":")[1])+45,
                        Integer.parseInt(data[6].split(":")[1]),
                        RssiType.ANCHOR_TO_BLIND);


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
