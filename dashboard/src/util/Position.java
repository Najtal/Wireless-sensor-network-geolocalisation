package util;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by jvdur on 19/05/2016.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(double[] xy) {
        this.x = (int) xy[0];
        this.y = (int) xy[1];
    }



    public Position(RealVector rvPosition) throws IllegalAccessException {
        if (rvPosition.getDimension() == 2) {
            this.x = (int) rvPosition.getEntry(0);
            this.x = (int) rvPosition.getEntry(1);
        } else {
            throw new IllegalAccessException("The RealVector should have two dimensions !");
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
