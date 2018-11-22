package cmsc420.structure.prquadtree;

import cmsc420.structure.City;

import java.awt.geom.Point2D;

//represents a leaf node in PR QuadTree
public class BlackNode extends Node {

    protected City city;

    public BlackNode() {
        super(Node.BLACK);
    }

    public City getCity() {
        return this.city;
    }
    @Override
    public Node add(City city, int width, int height, Point2D.Float origin) {
        if(this.city == null) {
            this.city = city;
            return this;
        } else {
            GreyNode greyNode = new GreyNode(width, height, origin);
            greyNode.add(city, width, height, origin);
            greyNode.add(this.city, width, height, origin);

            return greyNode;
        }
    }

    @Override
    public Node delete(City city, int width, int height, Point2D.Float origin) {
        if (this.city == city) {
            this.city = null;
            return WhiteNode.singleton;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
