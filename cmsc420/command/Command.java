/**
 * @(#)Command.java 1.1
 * <p>
 * 2014/09/09
 * @author Ruofei Du, Ben Zoller (University of Maryland, College Park), 2014
 * <p>
 * All rights reserved. Permission is granted for use and modification in CMSC420
 * at the University of Maryland.
 */
package cmsc420.command;

import cmsc420.geom.*;
import cmsc420.exception.CityAlreadyMappedException;
import cmsc420.exception.CityOutOfBoundsException;
import cmsc420.structure.City;
import cmsc420.structure.CityLocationComparator;
import cmsc420.structure.CityNameComparator;
import cmsc420.structure.prquadtree.BlackNode;
import cmsc420.structure.prquadtree.GreyNode;
import cmsc420.structure.prquadtree.Node;
import cmsc420.structure.prquadtree.PRQuadTree;
import cmsc420.structure.NodeDistance;
import cmsc420.structure.NodeDistanceComparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.css.Rect;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

//import cmsc420.structure.prquadtree.PRQuadTree;

/**
 * Processes each command in the MeeshQuest program. Takes in an XML command
 * node, processes the node, and outputs the results.
 *
 * @author Ben Zoller
 * @version 2.0, 23 Jan 2007
 */
public class Command {
    /** output DOM Document tree */
    protected Document results;

    /** root node of results document */
    protected Element resultsNode;

    /**
     * stores created cities sorted by their names (used with listCities command)
     */
    protected final TreeMap<String, City> citiesByName = new TreeMap<String, City>(new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }

    });

    /**
     * stores created cities sorted by their locations (used with listCities command)
     */
    protected final TreeSet<City> citiesByLocation = new TreeSet<City>(
            new CityLocationComparator());

    /**
     * stores all cities ever mapped in an TreeMap sorted by their name (new cities with same name overwrite old ones)
     */
    protected final TreeMap<City, Integer> allMappedCitiesByName = new TreeMap<City, Integer>(new Comparator<City>() {

        @Override
        public int compare(City o1, City o2) {
            return o2.getName().compareTo(o1.getName());
        }
    });

    /** spatial width and height of the MX Quadtree */
    protected int spatialWidth, spatialHeight;

    protected PRQuadTree prQuadTree = new PRQuadTree();

    /**
     * Set the DOM Document tree to send the of processed commands to.
     *
     * Creates the root results node.
     *
     * @param results
     *            DOM Document tree
     */
    public void setResults(Document results) {
        this.results = results;
        resultsNode = results.createElement("results");
        results.appendChild(resultsNode);
    }

    /**
     * Creates a command result element. Initializes the command name.
     *
     * @param node
     *            the command node to be processed
     * @return the results node for the command
     */
    private Element getCommandNode(final Element node) {
        final Element commandNode = results.createElement("command");
        commandNode.setAttribute("name", node.getNodeName());
        return commandNode;
    }

    /**
     * Processes an integer attribute for a command. Appends the parameter to
     * the parameters node of the results. Should not throw a number format
     * exception if the attribute has been defined to be an integer in the
     * schema and the XML has been validated beforehand.
     *
     * @param commandNode
     *            node containing information about the command
     * @param attributeName
     *            integer attribute to be processed
     * @param parametersNode
     *            node to append parameter information to
     * @return integer attribute value
     */
    private int processIntegerAttribute(final Element commandNode,
                                        final String attributeName, final Element parametersNode) {
        final String value = commandNode.getAttribute(attributeName);

        if (parametersNode != null) {
            /* add the parameters to results */
            final Element attributeNode = results.createElement(attributeName);
            attributeNode.setAttribute("value", value);
            parametersNode.appendChild(attributeNode);
        }

        /* return the integer value */
        return Integer.parseInt(value);
    }

    /**
     * Processes a string attribute for a command. Appends the parameter to the
     * parameters node of the results.
     *
     * @param commandNode
     *            node containing information about the command
     * @param attributeName
     *            string attribute to be processed
     * @param parametersNode
     *            node to append parameter information to
     * @return string attribute value
     */
    private String processStringAttribute(final Element commandNode,
                                          final String attributeName, final Element parametersNode) {
        final String value = commandNode.getAttribute(attributeName);

        if (parametersNode != null) {
            /* add parameters to results */
            final Element attributeNode = results.createElement(attributeName);
            attributeNode.setAttribute("value", value);
            parametersNode.appendChild(attributeNode);
        }

        /* return the string value */
        return value;
    }

    /**
     * Reports that the requested command could not be performed because of an
     * error. Appends information about the error to the results.
     *
     * @param type
     *            type of error that occurred
     * @param command
     *            command node being processed
     * @param parameters
     *            parameters of command
     */
    private void addErrorNode(final String type, final Element command,
                              final Element parameters) {
        final Element error = results.createElement("error");
        error.setAttribute("type", type);
        error.appendChild(command);
        error.appendChild(parameters);
        resultsNode.appendChild(error);
    }

    /**
     * Reports that a command was successfully performed. Appends the report to
     * the results.
     *
     * @param command
     *            command not being processed
     * @param parameters
     *            parameters used by the command
     * @param output
     *            any details to be reported about the command processed
     */
    private void addSuccessNode(final Element command,
                                final Element parameters, final Element output) {
        final Element success = results.createElement("success");
        success.appendChild(command);
        success.appendChild(parameters);
        success.appendChild(output);
        resultsNode.appendChild(success);
    }

    /**
     * Processes the commands node (root of all commands). Gets the spatial
     * width and height of the map and send the data to the appropriate data
     * structures.
     *
     * @param node
     *            commands node to be processed
     */
    public void processCommands(final Element node) {
        spatialWidth = Integer.parseInt(node.getAttribute("spatialWidth"));
        spatialHeight = Integer.parseInt(node.getAttribute("spatialHeight"));

        prQuadTree.setDimensions(spatialHeight, spatialWidth);
    }

    /**
     * Processes a createCity command. Creates a city in the dictionary (Note:
     * does not map the city). An error occurs if a city with that name or
     * location is already in the dictionary.
     *
     * @param node
     *            createCity node to be processed
     */
    public void processCreateCity(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");

        final String name = processStringAttribute(node, "name", parametersNode);
        final int x = processIntegerAttribute(node, "x", parametersNode);
        final int y = processIntegerAttribute(node, "y", parametersNode);
        final int radius = processIntegerAttribute(node, "radius",
                parametersNode);
        final String color = processStringAttribute(node, "color",
                parametersNode);

        /* create the city */
        final City city = new City(name, x, y, radius, color);

        if (citiesByLocation.contains(city)) {
            addErrorNode("duplicateCityCoordinates", commandNode,
                    parametersNode);
        } else if (citiesByName.containsKey(name)) {
            addErrorNode("duplicateCityName", commandNode, parametersNode);
        } else {
            final Element outputNode = results.createElement("output");

            /* add city to dictionary */
            citiesByName.put(name, city);
            citiesByLocation.add(city);

            /* add success node to results */
            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    /**
     * Clears all the data structures do there are not cities or roads in
     * existence in the dictionary or on the map.
     *
     * @param node
     *            clearAll node to be processed
     */
    public void processClearAll(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");

        /* clear data structures */
        citiesByName.clear();
        citiesByLocation.clear();

        prQuadTree.clearAll();
        allMappedCitiesByName.clear();
        /* add success node to results */
        addSuccessNode(commandNode, parametersNode, outputNode);
    }

    /**
     * Lists all the cities, either by name or by location.
     *
     * @param node
     *            listCities node to be processed
     */
    public void processListCities(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final String sortBy = processStringAttribute(node, "sortBy",
                parametersNode);

        if (citiesByName.isEmpty()) {
            addErrorNode("noCitiesToList", commandNode, parametersNode);
        } else {
            final Element outputNode = results.createElement("output");
            final Element cityListNode = results.createElement("cityList");

            Collection<City> cityCollection = null;
            if (sortBy.equals("name")) {
                cityCollection = citiesByName.values();
            } else if (sortBy.equals("coordinate")) {
                cityCollection = citiesByLocation;
            } else {
                /* XML validator failed */
                System.exit(-1);
            }

            for (City c : cityCollection) {
                addCityNode(cityListNode, c);
            }
            outputNode.appendChild(cityListNode);

            /* add success node to results */
            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    /**
     * Creates a city node containing information about a city. Appends the city
     * node to the passed in node.
     *
     * @param node
     *            node which the city node will be appended to
     * @param cityNodeName
     *            name of city node
     * @param city
     *            city which the city node will describe
     */
    private void addCityNode(final Element node, final String cityNodeName,
                             final City city) {
        final Element cityNode = results.createElement(cityNodeName);
        cityNode.setAttribute("name", city.getName());
        cityNode.setAttribute("x", Integer.toString((int) city.getX()));
        cityNode.setAttribute("y", Integer.toString((int) city.getY()));
        cityNode.setAttribute("radius", Integer
                .toString((int) city.getRadius()));
        cityNode.setAttribute("color", city.getColor());
        node.appendChild(cityNode);
    }

    /**
     * Creates a city node containing information about a city. Appends the city
     * node to the passed in node.
     *
     * @param node
     *            node which the city node will be appended to
     * @param city
     *            city which the city node will describe
     */
    private void addCityNode(final Element node, final City city) {
        addCityNode(node, "city", city);
    }

    public void processMapCity(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final String name = processStringAttribute(node, "name", parametersNode);

        final Element outputNode = results.createElement("output");

        if (!citiesByName.containsKey(name)) {
            addErrorNode("nameNotInDictionary", commandNode, parametersNode);
        } else if (prQuadTree.contains(name)) {
            addErrorNode("cityAlreadyMapped", commandNode, parametersNode);
        } else {
            City city = citiesByName.get(name);

            try {
                prQuadTree.add(city);
                allMappedCitiesByName.put(city, city.getRadius());

                addSuccessNode(commandNode, parametersNode, outputNode);

            } catch (CityOutOfBoundsException e) {
                addErrorNode("CityOutOfBounds", commandNode, parametersNode);
            } catch (CityAlreadyMappedException e) {
                addErrorNode("cityAlreadyMapped", commandNode, parametersNode);
            }
        }
    }

    public void processUnmapCity(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final String name = processStringAttribute(node, "name", parametersNode);

        final Element outputNode = results.createElement("output");

        if (!citiesByName.containsKey(name)) {
            addErrorNode("nameNotInDictionary", commandNode, parametersNode);
        } else if (!prQuadTree.contains(name)) {
            addErrorNode("cityNotMapped", commandNode, parametersNode);
        } else {

            prQuadTree.delete(citiesByName.get(name));

            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    public void processDeleteCity(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final String name = processStringAttribute(node, "name", parametersNode);

        if (!citiesByName.containsKey(name)) {
            addErrorNode("cityDoesNotExist", commandNode, parametersNode);
        } else {
            final City toDelete = citiesByName.get(name);
            final Element outputNode = results.createElement("output");

            if (prQuadTree.contains(name)) {
                prQuadTree.delete(toDelete);
                allMappedCitiesByName.remove(toDelete);
                addCityNode(outputNode, "cityUnmapped", toDelete);
            }

            citiesByName.remove(name);
            citiesByLocation.remove(toDelete);

            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    public void processPrintPRQuadtree(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");

        if (prQuadTree.isEmpty()) {
            addErrorNode("mapIsEmpty", commandNode, parametersNode);
        } else {
            final Element quadtreeNode = results.createElement("quadtree");
            //make helper method
            printPRQuadtree(quadtreeNode, prQuadTree.getRoot());
            outputNode.appendChild(quadtreeNode);

            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    private void printPRQuadtree(final Element outputNode, final Node curr) {
        if (curr.getType() != Node.WHITE) {
            if (curr.getType() == Node.BLACK) {

                final BlackNode black = (BlackNode) curr;
                final Element blackNode = results.createElement("black");

                blackNode.setAttribute("name", black.getCity().getName());
                blackNode.setAttribute("x", Integer.toString(black.getCity().getX()));
                blackNode.setAttribute("y", Integer.toString(black.getCity().getY()));

                outputNode.appendChild(blackNode);
            } else {
                final GreyNode grey = (GreyNode) curr;
                final Element greyNode = results.createElement("gray");

                greyNode.setAttribute("x", Integer.toString(grey.getCenterPointX()));
                greyNode.setAttribute("y", Integer.toString(grey.getCenterPointY()));

                for (int i = 0; i < 4; i++) {
                    printPRQuadtree(greyNode, grey.child(i));
                }

                outputNode.appendChild(greyNode);
            }
        } else {
            Element whiteNode = results.createElement("white");
            outputNode.appendChild(whiteNode);
        }
    }

    public void processSaveMap(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");

        final String name = processStringAttribute(node, "name", parametersNode);
        final Element outputNode = results.createElement("output");

        addSuccessNode(commandNode, parametersNode, outputNode);
    }

    public void processRangeCities(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");

        //final String name = processStringAttribute(node,"name", parametersNode);
        final Element outputNode = results.createElement("output");

        final int x = processIntegerAttribute(node, "x", parametersNode);
        final int y = processIntegerAttribute(node, "y", parametersNode);
        final int radius = processIntegerAttribute(node, "radius", parametersNode);

        String fname = new String();

        if (!node.getAttribute("saveMap").equals("")) {
            fname = processStringAttribute(node, "saveMap", parametersNode);
        }

        //make data structure to hold cities in range
        final TreeSet<City> inRange = new TreeSet<City>(new CityNameComparator());
        citiesInRange(prQuadTree.getRoot(), radius, new Point2D.Double(x, y), inRange);

        if (inRange.isEmpty()) {
            addErrorNode("noCitiesExistInRange", commandNode, parametersNode);
        } else {
            final Element cityListNode = results.createElement("cityList");

            for (City city : inRange) {
                addCityNode(cityListNode, city);
            }

            outputNode.appendChild(cityListNode);
            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }

    private void citiesInRange(final Node curr, final int radius, final Point2D.Double origin, TreeSet<City> inRange) {
        if (curr.getType() == Node.GREY) {
            GreyNode greyNode = (GreyNode) curr;

            //create circle with given origin and radius
            Circle2D.Double circle = new Circle2D.Double(origin.getX(), origin.getY(), radius);

            //need to check if any quadrants are in range of circle

            for (int i = 0; i < 4; i++) {
                Rectangle2D rect = greyNode.region(i);
                if (Inclusive2DIntersectionVerifier.intersects(rect, circle)) {
                    citiesInRange(greyNode.child(i), radius, origin, inRange);
                }
                /*
                if (inCircle(circle, greyNode.region(i))) {

                    //recurse till black node
                    citiesInRange(greyNode.child(i), radius, origin, inRange);
                }
                */
            }

        } else if (curr.getType() == Node.BLACK) {
            BlackNode blackNode = (BlackNode) curr;
            final City city = blackNode.getCity();
            Point2D cityDist = city.toPoint2D();
            double dist = origin.distance(cityDist);

            if (radius >= dist) {
                inRange.add(city);
            }
        }
    }

    private boolean inCircle(Circle2D circle, Rectangle2D rect) {

        double adjustedOriginX = rect.getX() - circle.getCenterX();
        double adjustedOriginY = rect.getY() - circle.getCenterY();
        final Rectangle2D.Double quadrant = new Rectangle2D.Double(adjustedOriginX, adjustedOriginY, rect.getWidth(), rect.getHeight());

        double rSquared = circle.getRadius() * circle.getRadius();

        if (quadrant.getMinX() > 0) { //right half of circle

            if (quadrant.getMinY() > 0) { //right and up (NE quad)
                double xSquared = quadrant.getMinX() * quadrant.getMinX();
                double ySquared = quadrant.getMinY() * quadrant.getMinY();

                return (xSquared + ySquared) <= rSquared;
            } else if (quadrant.getMaxY() < 0) { //right and down (SE quad)
                double xSquared = quadrant.getMinX() * quadrant.getMinX();
                double ySquared = quadrant.getMaxY() * quadrant.getMaxY();

                return (xSquared + ySquared <= rSquared);
            } else {
                return quadrant.getMinX() <= circle.getRadius();
            }

        } else if (quadrant.getMaxX() < 0) { //left half of circle
            if (quadrant.getMinY() > 0) { //left and up (NW quad)
                double xSquared = quadrant.getMaxX() * quadrant.getMaxX();
                double ySquared = quadrant.getMinY() * quadrant.getMinY();

                return (xSquared + ySquared <= rSquared);
            } else if (quadrant.getMaxY() < 0) { //left and down (SW quad)
                double xSquared = quadrant.getMaxX() * quadrant.getMaxX();
                double ySquared = quadrant.getMaxY() * quadrant.getMaxY();

                return xSquared + ySquared <= rSquared;
            } else {
                return Math.abs(quadrant.getMaxX()) <= circle.getRadius();
            }
        } else { //on y-axis
            if (quadrant.getMinY() > 0) {
                return quadrant.getMinY() <= circle.getRadius();
            } else if (quadrant.getMaxY() < 0) {
                return Math.abs(quadrant.getMaxY()) <= circle.getRadius();
            } else {
                return true;
            }
        }
    }

    public void processNearestCity(final Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");
        int x = processIntegerAttribute(node, "x", parametersNode);
        int y = processIntegerAttribute(node, "y", parametersNode);

        if (citiesByName.isEmpty() || prQuadTree.getRoot().getType() == Node.WHITE) {
            addErrorNode("mapIsEmpty", commandNode, parametersNode);
        } else {

            //make priority queue based on distance and city name
            Point2D.Float point = new Point2D.Float(x, y);
            PriorityQueue<NodeDistance> pq = new PriorityQueue(new NodeDistanceComparator());

            Node curr = prQuadTree.getRoot();
            pq.add(new NodeDistance(curr, point));
            curr = pq.poll().getNode();


            while (curr.getType() != Node.WHITE) {
                if (curr.getType() == Node.GREY) {
                    GreyNode greyNode = (GreyNode) curr;
                    for(int i = 0; i < 4; i++) {
                        Node child = greyNode.child(i);
                        if (child.getType() != Node.WHITE)
                            pq.add(new NodeDistance(child, point));
                    }
                } else if (curr.getType() == Node.BLACK) {
                    break;
                }

                curr = pq.poll().getNode();
            }

            addCityNode(outputNode, ((BlackNode)curr).getCity());

            addSuccessNode(commandNode, parametersNode, outputNode);

        }
    }

}

