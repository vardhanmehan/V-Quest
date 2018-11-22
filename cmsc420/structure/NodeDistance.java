package cmsc420.structure;


import cmsc420.geom.Shape2DDistanceCalculator;
import cmsc420.structure.prquadtree.BlackNode;
import cmsc420.structure.prquadtree.GreyNode;
import cmsc420.structure.prquadtree.Node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class NodeDistance {
    private Node node;
    private double distance;

    public NodeDistance(Node node, Point2D.Float point) {

        if (node.getType() == 0) {
            throw new IllegalArgumentException("Cannot have White Node");
        }
        this.node = node;

        if (node.getType() == Node.BLACK) {
            BlackNode currNode = (BlackNode) node;
            City city = currNode.getCity();

            Point2D.Float cityPoint = new Point2D.Float(city.getX(), city.getY());
            distance = point.distance(cityPoint);
        } else { //grey node
            GreyNode currNode = (GreyNode) node;
            Rectangle2D region = new Rectangle2D.Float(((GreyNode) node).getCenterPointX(),
                    ((GreyNode) node).getCenterPointY(),
                    ((GreyNode) node).getWidth(),
                    ((GreyNode) node).getHeight());

            distance = Shape2DDistanceCalculator.distance(point, region);
        }
    }

    public double getDistance() {
        return this.distance;
    }

    public Node getNode() {
        return this.node;
    }
}
