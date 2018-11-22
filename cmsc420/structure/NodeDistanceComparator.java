package cmsc420.structure;

import cmsc420.structure.prquadtree.BlackNode;
import cmsc420.structure.prquadtree.Node;

import java.util.Comparator;

public class NodeDistanceComparator implements Comparator<NodeDistance> {
    @Override
    public int compare(NodeDistance o1, NodeDistance o2) {
        if(o1.getDistance() < o2.getDistance()) {
            return -1;
        } else if(o1.getDistance() > o2.getDistance()) {
            return 1;
        } else { //if same distance, compare type of node then name if black nodes
            final Node node1 = o1.getNode();
            final Node node2 = o2.getNode();
            if (node1.getType() == Node.BLACK) {
                if (node2.getType() == Node.BLACK) {
                    final BlackNode n1 = (BlackNode) node1;
                    final BlackNode n2 = (BlackNode) node2;

                    int comp = n1.getCity().getName().compareTo(n2.getCity().getName());
                    comp = (-1) * (comp); //priority by descending ascii vals
                    return comp;
                } else { //curr node is Black node and node comparing to is Grey
                    return 1;
                }
            } else {
                if (node2.getType() == Node.BLACK) {
                    return -1;
                } else { //both grey nodes
                    return 0;
                }
            }
        }
    }

}
