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

}
