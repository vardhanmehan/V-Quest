package cmsc420.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class Inclusive2DIntersectionVerifier {
    public Inclusive2DIntersectionVerifier() {
    }

    public static boolean intersects(Point2D pt1, Point2D pt2) {
        return pt1.equals(pt2);
    }

    public static boolean intersects(Point2D point, Circle2D circle) {
        if (intersects((Point2D)point, (Rectangle2D)(new Double(circle.getX(), circle.getY(), circle.getWidth(), circle.getHeight())))) {
            double xdiff = point.getX() - circle.getCenterX();
            double ydiff = point.getY() - circle.getCenterY();
            return xdiff * xdiff + ydiff * ydiff <= circle.getRadius() * circle.getRadius();
        } else {
            return false;
        }
    }

    public static boolean intersects(Point2D point, Rectangle2D rect) {
        return point.getX() >= rect.getMinX() && point.getX() <= rect.getMaxX() && point.getY() >= rect.getMinY() && point.getY() <= rect.getMaxY();
    }

    public static boolean intersects(Point2D point, Line2D line) {
        return line.ptSegDistSq(point) == 0.0D;
    }

    public static boolean intersects(Line2D seg, Rectangle2D rect) {
        if (rect.intersectsLine(seg)) {
            return true;
        } else {
            Line2D[] sides = new Line2D[]{new java.awt.geom.Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMinX(), rect.getMaxY()), new java.awt.geom.Line2D.Double(rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMaxY()), new java.awt.geom.Line2D.Double(rect.getMaxX(), rect.getMaxY(), rect.getMaxX(), rect.getMinY()), new java.awt.geom.Line2D.Double(rect.getMaxX(), rect.getMinY(), rect.getMinX(), rect.getMinY())};

            for(int i = 0; i < 4; ++i) {
                if (intersects(seg, sides[i])) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean intersects(Line2D seg1, Line2D seg2) {
        return seg1.intersectsLine(seg2);
    }

    public static boolean intersects(Rectangle2D rect, Circle2D circle) {
        return Shape2DDistanceCalculator.distance(circle.getCenter(), rect) <= circle.getRadius();
    }

    public static boolean intersects(Circle2D c1, Circle2D c2) {
        double centerDistance = c1.getCenter().distance(c2.getCenter());
        double maxRadiusDistance = c1.getRadius() + c2.getRadius();
        return centerDistance <= maxRadiusDistance;
    }
}