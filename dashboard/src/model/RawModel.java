package model;

import bizz.Rssi;
import com.sun.xml.internal.bind.v2.TODO;

import java.util.Random;

/**
 * Created by jvdur on 09/05/2016.
 */
public class RawModel {


    private static RawModel instance;

    /**
     * Model singleton
     * @return
     */
    public static RawModel getInstance() {
        if (instance == null)
            instance = new RawModel();
        return instance;
    }

    /**
     * private cconstruuctor
     */
    private RawModel() {

        // TODO

    }


    /**
     * add new RSSI to the model.
     * @param nRssi a computed RSSI
     */
    public void addNewRssi(Rssi nRssi) {

        // TODO Must register that RSSI into the data structure

    }
}
