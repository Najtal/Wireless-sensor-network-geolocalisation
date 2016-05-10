package model;

/**
 * Created by jvdur on 09/05/2016.
 */
public class AnalyzeModel {

    private static AnalyzeModel instance;

    /**
     * Singleton getter
     * @return
     */
    public static AnalyzeModel getInstance() {
        if (instance == null)
            instance = new AnalyzeModel();
        return instance;
    }

    /**
     * private constructor
     */
    private AnalyzeModel() {

        // TODO: 10/05/2016

    }
}
