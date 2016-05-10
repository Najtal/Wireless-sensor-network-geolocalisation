package dataReader;

import bizz.BizzFactory;
import bizz.Rssi;
import model.RawModel;

/**
 * Created by jvdur on 10/05/2016.
 */
public class CliParser {

    public static String[] parseIncomingLine(String line) {

        if (line == null) {
            System.err.println("Parsing null line");
            return null;
        }

        /* Split line into components */
        return line.split(" ");
    }

    public static void handleData(String[] data) {


        switch (data[0]) {

            case "1" : // means it is a new RSSI mmeasurement between two anchors
                Rssi nRssi = BizzFactory.INSTANCE.createRssi(1,
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]));

                RawModel.getInstance().addNewRssi(nRssi);
                break;

        }


    }
}
