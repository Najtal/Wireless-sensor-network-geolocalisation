package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvdur on 01/06/2016.
 */
public class RawModelAARssiAvg {

    private List<List<List<Double>>> avgArray = new ArrayList<List<List<Double>>>(); // [from][to][#Rssi, SumRssi, avgRssi]

    public RawModelAARssiAvg() {
        avgArray = new ArrayList<List<List<Double>>>();
    }


    public void addRssiToAvg(int to, int from, int rssi) {
        if (avgArray.get(from) == null) {
            avgArray.add(from, new ArrayList<List<Double>>());
        }

        if (avgArray.get(from).get(to) == null) {
            avgArray.get(from).add(to, new ArrayList<Double>(3));
        }

        double nbRssi = avgArray.get(from).get(to).get(0) + 1;
        double sumRssi = avgArray.get(from).get(to).get(1) + rssi;

        avgArray.get(from).get(to).set(0, nbRssi);
        avgArray.get(from).get(to).set(1, sumRssi);
        avgArray.get(from).get(to).set(2, sumRssi/nbRssi);

    }

    public double avgRssi(int to, int from) {
        return avgArray.get(from).get(to).get(2);
    }

}
