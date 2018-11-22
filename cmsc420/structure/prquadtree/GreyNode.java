package cmsc420.structure.prquadtree;

import cmsc420.structure.City;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GreyNode extends Node{

    private Point2D.Float origin;
    private int width, height;
    private int halfWidth, halfHeight;

    private Node[] children;
    private Rectangle2D.Float[] quadrants;
    private Point2D.Float[] origins;

    public GreyNode(int width, int height, Point2D.Float origin) {
        super(Node.GREY);

        this.width = width;
        this.height = height;
        this.origin = origin;

        //since height and width are always divisible by 2
        this.halfWidth = this.width/2;
        this.halfHeight = this.height/2;

        //make all nodes empty children
        children = new Node[4];
        for(int i = 0; i < 4; i++) {
            children[i] = WhiteNode.singleton;
        }

        //set quadrant and quadrant origin specifications
        quadrants = new Rectangle2D.Float[4];
        origins = new Point2D.Float[4];

        Point2D.Float northWestOrigin = new Point2D.Float(origin.x, origin.y + halfHeight);
        quadrants[0] = new Rectangle2D.Float(northWestOrigin.x, northWestOrigin.y, halfWidth, halfHeight);
        origins[0] = northWestOrigin;

        Point2D.Float northEastOrigin = new Point2D.Float(origin.x + halfWidth, origin.y + halfHeight);
        quadrants[1] = new Rectangle2D.Float(northEastOrigin.x, northEastOrigin.y, halfWidth, halfHeight);
        origins[1] = northEastOrigin;

        Point2D.Float southWestOrigin = new Point2D.Float(origin.x, origin.y);
        quadrants[2] = new Rectangle2D.Float(southWestOrigin.x, southWestOrigin.y, halfWidth, halfHeight);
        origins[2] = southWestOrigin;

        Point2D.Float southEastOrigin = new Point2D.Float(origin.x + halfWidth, origin.y);
        quadrants[3] = new Rectangle2D.Float(southEastOrigin.x, southEastOrigin.y, halfWidth, halfHeight);
        origins[3] = southEastOrigin;
    }

    public int getCenterPointX() {
        return (int)origin.getX() + halfWidth;
    }

    public int getCenterPointY() {
        return (int)origin.getY() + halfHeight;
    }
    //greater is inclusive, less is exclusive
    @Override
    public Node add(City city, int width, int height, Point2D.Float origin) {
        final Point2D location = city.toPoint2D();

        for(int i = 0; i < 4; i++) {
            if(location.getX() >= quadrants[i].getMinX() && location.getX() < quadrants[i].getMaxX()
                    && location.getY() >= quadrants[i].getMinY() && location.getY() < quadrants[i].getMaxY()){

                children[i] = children[i].add(city, halfWidth, halfHeight, origins[i]);
                break;
            }
        }

        return this;
    }

    @Override
    public Node delete(City city, int width, int height, Point2D.Float origin) {
        Point2D location = city.toPoint2D();

        for(int i = 0; i < 4; i++) {
            if(location.getX() >= quadrants[i].getMinX() && location.getX() < quadrants[i].getMaxX()
                    && location.getY() >= quadrants[i].getMinY() && location.getY() < quadrants[i].getMaxY()){
                children[i] = children[i].delete(city, halfWidth, halfHeight, origins[i]);

            }
        }

        /*if (numChildren == 1) {
            return black node
        } else if (numChildren == 0) {
            return white node
        } else {
            return this
        }
         */

        if (emptyChildren() == 3 && blackNodes() == 1) {
            for (int i = 0; i < 4; i++) {
                if(children[i].getType() == Node.BLACK) {
                    return children[i];
                }
            }

            return null; //so code compiles, will never actually return null if numChildren() == 1
        } else if (emptyChildren() == 4) {
            return WhiteNode.singleton;
        } else {
            return this;
        }
    }

    public Node child(int i) {
        return children[i];
    }
    private int emptyChildren() {
        int res = 0;

        for (Node n: children) {
            if (n == WhiteNode.singleton) {
                res++;
            }
        }

        return res;
    }

    private int blackNodes() {
        int numBlack = 0;
        for(Node n: children) {
            if(n.getType() == Node.BLACK) {
                numBlack++;
            }
        }

        return numBlack;
    }

    public Rectangle2D region(int i) {
        return quadrants[i];
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
