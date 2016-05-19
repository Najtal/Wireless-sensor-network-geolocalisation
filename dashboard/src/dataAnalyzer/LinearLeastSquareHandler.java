package dataAnalyzer;

import constant.RssiType;
import model.AnalyzeModel;
import model.AnchorModel;
import model.RawModel;
import model.RawModelSequence;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;
import ucc.AnchorDTO;
import ucc.RssiDTO;
import util.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvdur on 19/05/2016.
 */
public class LinearLeastSquareHandler {

    public static final LinearLeastSquareHandler INSTANCE = new LinearLeastSquareHandler();

    /**
     * Create a thread that read the raw values and compute the blind position
     * @param sequence the sequence of raw data
     * @param rm the raw model (where to get the raw values
     * @param am the analyzed model (where to save the blind position)
     * @param anchors the anchors
     * @param sequenceNumber the sequence number of the data set
     */
    public void analyzeSequenceRawData(RawModelSequence sequence,
                                       RawModel rm,
                                       AnalyzeModel am,
                                       AnchorModel anchors,
                                       int sequenceNumber) {

        Thread thread = new Thread(){
            public void run(){

                List<RssiDTO> measurements = new ArrayList<>();
                measurements.addAll(sequence.getRssiAtoB());
                measurements.addAll(sequence.getRssiBtoA());
                Position blindPosition = getPositionFromRSSI(anchors, measurements);

                am.addBlindPosition(blindPosition, sequenceNumber);

                rm.removeSequence(sequenceNumber);
            }
        };

        thread.start();
    }


    /**
     * getPositionFromRSSI computes through the TrilaterationFunction the position of the BLIND
     * @param measurements All measurements from ANCHOR to BLIND and BLIND to ANCHOR
     * @return the optimum position of the BLIND
     */
    private static Position getPositionFromRSSI(AnchorModel anchors, List<RssiDTO> measurements) {

        int inc = 0;

        ArrayList<Double> rssiPosAnchorX = new ArrayList<Double>();
        ArrayList<Double> rssiPosAnchorY = new ArrayList<Double>();
        ArrayList<Double> rssiDistToBlind = new ArrayList<Double>();

        AnchorDTO aTmp;

        for (RssiDTO rssi : measurements) {
            if (rssi.getType() == RssiType.ANCHOR_TO_BLIND) {
                aTmp = anchors.getAnchorById(rssi.getFrom());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosx());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosy());
                rssiDistToBlind.add(inc, (double)rssi.getDistanceMeters());
                inc++;
            } else if (rssi.getType() == RssiType.BLIND_TO_ANCHOR) {
                aTmp = anchors.getAnchorById(rssi.getTo());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosx());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosy());
                rssiDistToBlind.add(inc, (double)rssi.getDistanceMeters());
                inc++;
            }
        }

        double[][] positions = new double[inc][2];
        double[] distances = new double[inc];

        for(int i=0; i<inc ; i++) {
            positions[i][0] = rssiPosAnchorX.get(i);
            positions[i][1] = rssiPosAnchorY.get(i);
            distances[i] = rssiDistToBlind.get(i);
        }

        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
        LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(trilaterationFunction);
        //NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());

        RealVector rvPosition = lSolver.solve();
        //LeastSquaresOptimizer.Optimum optimum = nlSolver.solve();

        Position position = null;
        try {
            position = new Position(rvPosition);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return position;
    }

}
