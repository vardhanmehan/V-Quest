package cmsc420.structure.prquadtree;

import cmsc420.structure.City;

import java.awt.geom.Point2D;

//empty node in PR QuadTree
public class WhiteNode extends Node {

    //creates singleton for empty node
    public static WhiteNode singleton = new WhiteNode();

    //initializer
    public WhiteNode() {
        super(Node.WHITE);
    }

    public static WhiteNode getInstance() {
        return singleton;
    }

    @Override
    public Node add(City city, int width, int height, Point2D.Float origin) {
        //make black node
        Node blackNode = new BlackNode();
        return blackNode.add(city, width, height, origin);
    }

    @Override
    public Node delete(City city, int width, int height, Point2D.Float origin) {
        //impossible
        throw new IllegalArgumentException();
    }
}
