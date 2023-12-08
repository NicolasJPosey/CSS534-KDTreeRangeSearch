package edu.uwb.css534;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uw.bothell.css.dsl.MASS.VertexPlace;

import java.util.Collections;


/** 
 * class representing a node in a KD-Tree
 */
// class KDNode  extends VertexPlace {
//     // label for node
//     private long vertexId;
//     // store point as a Tuple2D
//     private Tuple2D location; 
//     // left and right child nodes of the KDNode - representingt the subdivision of the space
//     private KDNode leftChild, rightChild;

//     /**
//      * constructor for KDNode
//      * @param location The 2D point that this node will represent
//      */
//     public KDNode(Tuple2D location) {
//         this.vertexId = 0;
//         this.location = location;
//     }

//     /**
//      * constructor for KDNode
//      * @param location The 2D point that this node will represent
//      */
//     public KDNode(Object arg) {
//         super();
//         this.vertexId = 0;
//         this.location = (Tuple2D) arg;
//     }

//     /**
//      * constructor for KDNode
//      * @param nodeInfo Information about the node such as it's ID and location that the node will represent
//      */
//     public KDNode(VertexTriplet nodeInfo) {
//         this.vertexId = nodeInfo.getVertexId();
//         this.location = new Tuple2D(nodeInfo.getX(), nodeInfo.getY());
//     }
     
//     /**
//      * retrieves the location of this node
//      * @return the Tuple2D instance representing this node's location in the KD-Tree
//      */
//     public Tuple2D getLocation() {
//         return location;
//     }

//     /**
//      * sets the location of this node
//      * @param location the new location (Tuple2D) for this node
//      */
//     public void setLocation(Tuple2D location) {
//         this.location = location;
//     }
     
//     /**
//      * retrieves the vertexId of this node
//      * @return the vertex id for the node
//      */
//     public long getVertexId() {
//         return vertexId;
//     }

//     /**
//      * sets the vertexId of this node
//      * @param vertexId the new vertexId for this node
//      */
//     public void setVertexId(long vertexId) {
//         this.vertexId = vertexId;
//     }
    
//     /**
//      * retrieves the left child of this node
//      * @return the left child node
//      */
//     public KDNode getLeftChild() {
//         return leftChild;
//     }

//     /**
//      * sets the left child of this node
//      * @param leftChild the node to be set as the left child
//      */
//     public void setLeftChild(KDNode leftChild) {
//         this.leftChild = leftChild;
//     }

//     /**
//      * retrieves the right child of this node
//      * @return the right child node
//      */
//     public KDNode getRightChild() {
//         return rightChild;
//     }

//     /**
//      * sets the right child of this node
//      * @param leftChild the node to be set as the right child
//      */
//     public void setRightChild(KDNode rightChild) {
//         this.rightChild = rightChild;
//     }
// }



/**
 * main class for KD-Tree operations
 */
public class KDTree {

    private static final int K = 2; // for 2D points
    
    // /**
    //  * builds a KD-Tree from a list of points 
    //  * @param points a list of Tuple2D points to be inserted into the tree
    //  * @param depth the current depth of the tree, used for determining the axis of division
    //  * @return the root node of the constructed KD-Tree
    //  */
    public static KDNode buildKDTree(List<Tuple2D> points, int depth) {
        // check if the list is empty, return null.
        if (points.isEmpty()) {
            return null;
        }
        // determine the axis for splitting (x-axis or y-axis)
        int axis = depth % K; 

        // sort the points based on the determined axis
        if (axis == 0) {
            points.sort(Comparator.comparingInt(Tuple2D::getX));
        } else {
            points.sort(Comparator.comparingInt(Tuple2D::getY));
        }

        // find the median index
        int medianIndex = points.size() / 2; 
        // get the median point
        Tuple2D median = points.get(medianIndex);
        // create a new node with the median point
        KDNode node = new KDNode(median); 

        //build the left subtree using the left half of the points, increase depth by 1
        // node.setLeftChild( buildKDTree(points.subList(0, medianIndex), depth + 1));
        //build the right subtree using the right half of the points, increase depth by 1
        // node.setRightChild( buildKDTree(points.subList(medianIndex + 1, points.size()), depth + 1));

        return node; // return the root of the KD-Tree
    }

    /**
     * reads points from a specified file and converts them into a list of Tuple2D objects
     * @param filename the name of the file containing the points
     * @return a list of Tuple2D objects representing the points read from the file
     */
    public static List<Tuple2D> readPointsFromFile(String filename) {
        List<Tuple2D> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // read each line from the file
            while ((line = reader.readLine()) != null) {
                // convert the line to a Tuple2D object
                Tuple2D point = parseLineToTuple(line);
                // check if the line is valid and converts successfully, add the point to the lis
                if (point != null) {
                    points.add(point);
                }
            }
        } catch (IOException e) {
            // handle any io exceptions during file reading
            System.err.println("Error reading from file: " + e.getMessage());
        }
         // return the list of points
        return points;
    }
    /**
     * parses a single line of text into a VertexTriplet object
     * the line format is expected to be vertexId:(x, y)
     * @param line A string representing a line in the file
     * @return A VertexTriplet object if the line can be parsed, null otherwise
     */
    private static Tuple2D parseLineToTuple(String line) {
        // remove parentheses and split by comma
        String[] parts = line.split(":");
        String[] piontParts = parts[1].replaceAll("[()]", "").split(",\\s*");
        if (parts.length == 2) {
            try {
                // parse the x and y coordinates from the string parts
                int id = Integer.parseInt(parts[0].trim());
                int x = Integer.parseInt(piontParts[0].trim());
                int y = Integer.parseInt(piontParts[1].trim());

                // return a new Tuple2D object with these coordinates
                return new Tuple2D(id, x, y);
            } catch (NumberFormatException e) {
                // handle any number format exceptions during parsing
                System.err.println("Invalid number format in line: " + line);
            }
        }
        // return null if the line cannot be parsed into a Tuple2D
        return null; 
    }
    
}