package dataAnalyzer;

import constant.RssiType;
import model.AnchorModel;
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


    /**
     * getPositionFromRSSI computes through the TrilaterationFunction the position of the BLIND
     * @param measurements All measurements from ANCHOR to BLIND and BLIND to ANCHOR
     * @return the optimum position of the BLIND
     */
    public Position getPositionFromRSSI(AnchorModel anchors, List<RssiDTO> measurements) {

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
