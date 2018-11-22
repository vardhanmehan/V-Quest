package cmsc420.structure.prquadtree;

import cmsc420.structure.City;

import java.awt.*;
import java.awt.geom.Point2D;

/*abstract class as this is just the blueprint for the rest of the nodes*/
public abstract class Node {

    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int GREY = 2;

    public final int type;

    public Node(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public abstract Node add(City city, int width, int height, Point2D.Float origin);

    public abstract Node delete(City city, int width, int height, Point2D.Float origin);
}
