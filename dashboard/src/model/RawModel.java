package model;

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
     * This way we add new data in the model.
     * @param data
     */
    public void fetchNewData(String[] data) {

        // TODO save the data in a data structure defined depending on how data are printed out from the Gateway mote

    }
}
