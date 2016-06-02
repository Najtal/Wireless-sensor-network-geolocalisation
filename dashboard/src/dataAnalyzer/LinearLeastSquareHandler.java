package dataAnalyzer;

import app.AppContext;
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
import util.Log;
import util.Position;
import util.PositionDouble;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvdur on 19/05/2016.
 */
public class LinearLeastSquareHandler {

    //public static final LinearLeastSquareHandler INSTANCE = new LinearLeastSquareHandler();
    public static final int TIME_TO_WAIT_AT_START = 1000*Integer.parseInt(AppContext.INSTANCE.getProperty("waitBeforeAnalyze"));

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

                Log.logInfo("ARD WAIT seqno: " + sequenceNumber);


                try {
                    this.sleep(TIME_TO_WAIT_AT_START);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Log.logInfo("ARD START seqno: " + sequenceNumber);

                List<RssiDTO> measurements = new ArrayList<>();
                measurements.addAll(sequence.getRssiAtoB());
                measurements.addAll(sequence.getRssiBtoA());
                if (measurements.size() < 3) {
                    return;
                }
                PositionDouble blindPosition = getPositionFromRSSI(anchors, measurements);

                am.addBlindPosition(blindPosition, sequenceNumber);

                rm.removeSequence(sequenceNumber);

                Log.logInfo("ARD DONE seqno: " + sequenceNumber + " POS: " + blindPosition.getX()+"x"+blindPosition.getY());
            }
        };

        thread.start();
    }


    /**
     * getPositionFromRSSI computes through the TrilaterationFunction the position of the BLIND
     * @param measurements All measurements from ANCHOR to BLIND and BLIND to ANCHOR
     * @return the optimum position of the BLIND
     */
    private static PositionDouble getPositionFromRSSI(AnchorModel anchors, List<RssiDTO> measurements) {

        int inc = 0;

        ArrayList<Double> rssiPosAnchorX = new ArrayList<Double>();
        ArrayList<Double> rssiPosAnchorY = new ArrayList<Double>();
        ArrayList<Double> rssiDistToBlind = new ArrayList<Double>();

        AnchorDTO aTmp;

        for (RssiDTO rssi : measurements) {

            Log.logInfo("ANALYZE - FROM:" + rssi.getFrom() + " TO:" + rssi.getTo() + " RSSI:"+rssi.getRssi() );

            if (rssi.getType() == RssiType.ANCHOR_TO_BLIND) {
                aTmp = anchors.getAnchorById(rssi.getFrom());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosx());
                rssiPosAnchorY.add(inc, (double)aTmp.getPosy());
                rssiDistToBlind.add(inc, (double)rssi.getDistanceMeters());
                inc++;
            } else if (rssi.getType() == RssiType.BLIND_TO_ANCHOR) {
                aTmp = anchors.getAnchorById(rssi.getTo());
                rssiPosAnchorX.add(inc, (double)aTmp.getPosx());
                rssiPosAnchorY.add(inc, (double)aTmp.getPosy());
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
        //LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(trilaterationFunction);
        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());

        PositionDouble position = null;

        try {
            double[] centroid = nlSolver.solve().getPoint().toArray();
            position = new PositionDouble(centroid);

        } catch (Exception e) {
            System.out.println("Error while computing the position");
            e.printStackTrace();
        }

        return position;
    }

}
