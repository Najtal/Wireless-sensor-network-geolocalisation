package util;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by jvdur on 19/05/2016.
 */
public class PositionDouble {

    private double x;
    private double y;

    public PositionDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PositionDouble(double[] xy) {
        this.x = xy[0];
        this.y = xy[1];
    }



    public PositionDouble(RealVector rvPosition) throws IllegalAccessException {
        if (rvPosition.getDimension() == 2) {
            this.x = rvPosition.getEntry(0);
            this.x = rvPosition.getEntry(1);
        } else {
            throw new IllegalAccessException("The RealVector should have two dimensions !");
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
