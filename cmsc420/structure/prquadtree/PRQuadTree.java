package cmsc420.structure.prquadtree;
import cmsc420.exception.CityAlreadyMappedException;
import cmsc420.exception.CityOutOfBoundsException;
import cmsc420.structure.City;
import java.awt.geom.Point2D;
import java.util.HashSet;

public class PRQuadTree {

    private Node root;
    private int spatialHeight, spatialWidth;
    private Point2D.Float origin;
    private HashSet<String> cities;

    public PRQuadTree() {
        root = WhiteNode.singleton;
        origin = new Point2D.Float(0, 0);
        cities = new HashSet<String>();
    }

    public void setDimensions(int height, int width) {

        this.spatialHeight = height;
        this.spatialWidth = width;
    }

    public Node getRoot() {
        return this.root;
    }

    public int getSpatialHeight() {
        return this.spatialHeight;
    }

    public int getSpatialWidth() {
        return this.spatialWidth;
    }

    public boolean contains(String name) {
        return cities.contains(name);
    }
    public void add(City city) throws CityAlreadyMappedException, CityOutOfBoundsException {
        if(cities.contains(city.getName())) {
            throw new CityAlreadyMappedException();
        } else {

            if (city.getX() < origin.getX() || city.getY() < origin.getY()
                    || city.getX() >= spatialWidth || city.getY() >= spatialHeight) {
                throw new CityOutOfBoundsException();
            }

            cities.add(city.getName());
            root = root.add(city, spatialWidth, spatialHeight, origin);
        }
    }

    public boolean delete(City city) {
        if(!cities.contains(city.getName())) {
            return false;
        }

        root = root.delete(city, spatialWidth, spatialHeight, origin);
        cities.remove(city.getName());

        return true;
    }

    public void clearAll() {
        root = WhiteNode.singleton;
        cities.clear();
    }

    public boolean isEmpty() {
        return root == WhiteNode.singleton;
    }


}
