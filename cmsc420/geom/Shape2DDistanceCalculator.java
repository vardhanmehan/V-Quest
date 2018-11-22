//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cmsc420.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Shape2DDistanceCalculator {
    public Shape2DDistanceCalculator() {
    }

    public static double distance(Point2D pt, Rectangle2D rect) {
        double distanceSq = 0.0D;
        double ydist;
        if (pt.getX() < rect.getMinX()) {
            ydist = rect.getMinX() - pt.getX();
            distanceSq += ydist * ydist;
        } else if (pt.getX() > rect.getMaxX()) {
            ydist = pt.getX() - rect.getMaxX();
            distanceSq += ydist * ydist;
        }

        if (pt.getY() < rect.getMinY()) {
            ydist = rect.getMinY() - pt.getY();
            distanceSq += ydist * ydist;
        } else if (pt.getY() > rect.getMaxY()) {
            ydist = pt.getY() - rect.getMaxY();
            distanceSq += ydist * ydist;
        }

        return Math.sqrt(distanceSq);
    }
}
