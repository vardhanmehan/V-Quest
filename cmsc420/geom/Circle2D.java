package cmsc420.geom;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Circle2D extends Ellipse2D implements Geometry2D {
    public Circle2D() {
    }

    public int getType() {
        return 3;
    }

    public Point2D getCenter() {
        return new java.awt.geom.Point2D.Double(this.getCenterX(), this.getCenterY());
    }

    public abstract double getRadius();

    public abstract void setCenter(Point2D var1);

    public abstract void setRadius(double var1);

    public static class Double extends Circle2D {
        protected java.awt.geom.Ellipse2D.Double circle;

        public Double(double centerX, double centerY, double radius) {
            double diameter = radius * 2.0D;
            this.circle = new java.awt.geom.Ellipse2D.Double(centerX - radius, centerY - radius, diameter, diameter);
        }

        public Double(java.awt.geom.Point2D.Double center, double radius) {
            this(center.x, center.y, radius);
        }

        public double getRadius() {
            return this.circle.width * 0.5D;
        }

        public double getCenterX() {
            return this.circle.getCenterX();
        }

        public double getCenterY() {
            return this.circle.getCenterY();
        }

        public double getHeight() {
            return this.circle.height;
        }

        public double getWidth() {
            return this.circle.width;
        }

        public double getX() {
            return this.circle.x;
        }

        public double getY() {
            return this.circle.y;
        }

        public void setCenter(Point2D point) {
            this.circle.x = point.getX() - this.circle.width;
            this.circle.y = point.getY() - this.circle.height;
        }

        public void setRadius(double radius) {
            this.circle.width = this.circle.height = radius * 2.0D;
        }

        public boolean isEmpty() {
            return this.circle.isEmpty();
        }

        public void setFrame(double x, double y, double w, double h) {
            if (w != h) {
                throw new IllegalArgumentException("width must be equal to height");
            } else {
                this.circle.setFrame(x, y, w, h);
            }
        }

        public Rectangle2D getBounds2D() {
            return this.circle.getBounds2D();
        }
    }

    public static class Float extends Circle2D {
        protected java.awt.geom.Ellipse2D.Float circle;

        public Float(float centerX, float centerY, float radius) {
            float diameter = radius * 2.0F;
            this.circle = new java.awt.geom.Ellipse2D.Float(centerX - radius, centerY - radius, diameter, diameter);
        }

        public Float(java.awt.geom.Point2D.Float center, float radius) {
            this(center.x, center.y, radius);
        }

        public double getRadius() {
            return (double)this.circle.width * 0.5D;
        }

        public double getCenterX() {
            return this.circle.getCenterX();
        }

        public double getCenterY() {
            return this.circle.getCenterY();
        }

        public double getHeight() {
            return (double)this.circle.height;
        }

        public double getWidth() {
            return (double)this.circle.width;
        }

        public double getX() {
            return (double)this.circle.x;
        }

        public double getY() {
            return (double)this.circle.y;
        }

        public void setCenter(Point2D point) {
            this.circle.x = (float)point.getX() - this.circle.width;
            this.circle.y = (float)point.getY() - this.circle.height;
        }

        public void setRadius(double radius) {
            this.circle.width = this.circle.height = (float)radius * 2.0F;
        }

        public boolean isEmpty() {
            return this.circle.isEmpty();
        }

        public void setFrame(double x, double y, double w, double h) {
            if (w != h) {
                throw new IllegalArgumentException("width must be equal to height");
            } else {
                this.circle.setFrame(x, y, w, h);
            }
        }

        public Rectangle2D getBounds2D() {
            return this.circle.getBounds2D();
        }
    }
}
