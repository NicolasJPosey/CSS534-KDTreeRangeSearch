import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/** 
 * class representing a node in a KD-Tree
 */
class KDNode {
    // label for node
    private long vertexId;
    // store point as a Tuple2D
    private Tuple2D location; 
    // left and right child nodes of the KDNode - representingt the subdivision of the space
    private KDNode leftChild, rightChild;

    /**
     * constructor for KDNode
     * @param location The 2D point that this node will represent
     */
    public KDNode(Tuple2D location) {
        this.vertexId = 0;
        this.location = location;
    }
    /**
     * constructor for KDNode
     * @param nodeInfo Information about the node such as it's ID and location that the node will represent
     */
    public KDNode(VertexTriplet nodeInfo) {
        this.vertexId = nodeInfo.getVertexId();
        this.location = new Tuple2D(nodeInfo.getX(), nodeInfo.getY());
    }
     
    /**
     * retrieves the location of this node
     * @return the Tuple2D instance representing this node's location in the KD-Tree
     */
    public Tuple2D getLocation() {
        return location;
    }

    /**
     * sets the location of this node
     * @param location the new location (Tuple2D) for this node
     */
    public void setLocation(Tuple2D location) {
        this.location = location;
    }
     
    /**
     * retrieves the vertexId of this node
     * @return the vertex id for the node
     */
    public long getVertexId() {
        return vertexId;
    }

    /**
     * sets the vertexId of this node
     * @param vertexId the new vertexId for this node
     */
    public void setVertexId(long vertexId) {
        this.vertexId = vertexId;
    }
    
    /**
     * retrieves the left child of this node
     * @return the left child node
     */
    public KDNode getLeftChild() {
        return leftChild;
    }

    /**
     * sets the left child of this node
     * @param leftChild the node to be set as the left child
     */
    public void setLeftChild(KDNode leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * retrieves the right child of this node
     * @return the right child node
     */
    public KDNode getRightChild() {
        return rightChild;
    }

    /**
     * sets the right child of this node
     * @param leftChild the node to be set as the right child
     */
    public void setRightChild(KDNode rightChild) {
        this.rightChild = rightChild;
    }
}

/**
 * class representing a 2D range for range queries in KD-Tree
 */
class Range {
    // lower-left corner of the range - representing the minimum x and y values
    private final Tuple2D lowerLeft; 
    // upper-right corner of the range - representing the maximum x and y values
    private final Tuple2D upperRight;
    
    /**
     * constructor for the Range class.
     * @param lowerLeft the lower-left corner of the range
     * @param upperRight the upper-right corner of the range
     */
    public Range(Tuple2D lowerLeft, Tuple2D upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }
    /**
     * checks if the range contains a given point
     * @param point the point to check
     * @return true if the point is within the range, false otherwise
     */
    public boolean contains(Tuple2D point) {
        return point.getX() >= lowerLeft.getX() && point.getX() <= upperRight.getX() &&
               point.getY() >= lowerLeft.getY() && point.getY() <= upperRight.getY();
    }
    /**
     * checks if this range intersects with another given range
     * @param searchRegion the range to check for intersection with
     * @return true if the ranges intersect, false otherwise
     */
    public boolean intersects(Range searchRegion ) {
        return this.lowerLeft.getX() <= searchRegion.upperRight.getX() &&
               this.upperRight.getX() >= searchRegion.lowerLeft.getX() &&
               this.lowerLeft.getY() <= searchRegion.upperRight.getY() &&
               this.upperRight.getY() >= searchRegion.lowerLeft.getY();
    }

    /**
     * retrieves the lower-left corner of the range
     * @return the lower-left corner as Tuple2D
     */
    public Tuple2D getLowerLeft() {
        return lowerLeft;
    }

    /**
     * retrieves the upper-right corner of the range
     * @return the upper-right corner as Tuple2D
     */
    public Tuple2D getUpperRight() {
        return upperRight;
    }
}

/**
 * main class for KD-Tree operations
 */
public class KDTree {

    private static final int K = 2; // for 2D points
    
    /**
     * builds a KD-Tree from a list of points 
     * @param points a list of Tuple2D points to be inserted into the tree
     * @param depth the current depth of the tree, used for determining the axis of division
     * @return the root node of the constructed KD-Tree
     */
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
        node.setLeftChild( buildKDTree(points.subList(0, medianIndex), depth + 1));
        //build the right subtree using the right half of the points, increase depth by 1
        node.setRightChild( buildKDTree(points.subList(medianIndex + 1, points.size()), depth + 1));

        return node; // return the root of the KD-Tree
    }

    /**
     * builds a KD-Tree from a list of points 
     * @param points a list of Triplet points to be inserted into the tree
     * @param depth the current depth of the tree, used for determining the axis of division
     * @return the root node of the constructed KD-Tree
     */
    public static KDNode buildKDTreeFromTriplets(List<VertexTriplet> points, int depth) {
        // check if the list is empty, return null.
        if (points.isEmpty()) {
            return null;
        }
        // determine the axis for splitting (x-axis or y-axis)
        int axis = depth % K; 

        // sort the points based on the determined axis
        if (axis == 0) {
            Collections.sort(points, new TripletComparatorX());
        } else {
            Collections.sort(points, new TripletComparatorY());
        }

        // find the median index
        int medianIndex = points.size() / 2; 
        // get the median point
        VertexTriplet median = points.get(medianIndex);
        // create a new node with the median point
        KDNode node = new KDNode(median); 

        //build the left subtree using the left half of the points, increase depth by 1
        node.setLeftChild( buildKDTreeFromTriplets(points.subList(0, medianIndex), depth + 1));
        //build the right subtree using the right half of the points, increase depth by 1
        node.setRightChild( buildKDTreeFromTriplets(points.subList(medianIndex + 1, points.size()), depth + 1));

        return node; // return the root of the KD-Tree
    }

    /**
     * prints the structure of the KD-Tree to the provided PrintStream
     * the tree is printed in a pre-order traversal manner, showing the hierarchy of nodes
     * - based on https://www.baeldung.com/java-print-binary-tree-diagram
     * @param root the root node of the KD-Tree to be printed
     * @param depth the starting depth of the tree, usually 0
     * @param os the PrintStream to which the tree structure will be printed
     */
    public static void printKDTree(KDNode root, int depth, PrintStream os) {
        
        StringBuilder sb = new StringBuilder();
        traversePreOrder(sb, "", "", root, depth);
        // print the constructed tree diagram
        os.print(sb.toString()); 
    }
    /**
     * helper method for recursive pre-order traversal of the KD-Tree
     * this method builds a string representation of the tree with indentation and lines indicating parent-child relationships
     *  - based on https://www.baeldung.com/java-print-binary-tree-diagram
     * @param sb StringBuilder used to build the string representation of the tree
     * @param padding padding (indentation) to align the nodes of the tree
     * @param pointer a string representation of the lines connecting parent and child nodes
     * @param node the current node being visited
     * @param depth the depth of the current node in the tree.
     */
    public static void traversePreOrder(StringBuilder sb, String padding, String pointer, KDNode node, int depth) {

        if (node != null) {
            String axis = depth % K == 0 ? ":x axis " : ":y axis";
            // append current node's information to StringBuilder
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getLocation() + axis);
            sb.append("\n");

            // build padding string for child nodes
            StringBuilder paddingBuilder = new StringBuilder(padding);
            paddingBuilder.append("│  ");
    
            String paddingForBoth = paddingBuilder.toString();
            // pointer for right child
            String pointerForRight = "└──"; 
            // pointer for left child
            String pointerForLeft = (node.getRightChild() != null) ? "├──" : "└──"; 
            // traverse left and right children
            traversePreOrder(sb, paddingForBoth, pointerForLeft, node.getLeftChild(), depth+1);
            traversePreOrder(sb, paddingForBoth, pointerForRight, node.getRightChild(), depth+1);
        }
    }
    /**
     * helper method for getting the edges of the tree
     * @param node the current node being visited.
     * @param sourceId the vertexId of the root node.
     * @param edgeList list of edges in the tree.
     */
    public static void getEdgesOfTree(KDNode node, long sourceId, List<EdgePair> edgeList) {
        if (node != null) {
            // traverse left and right children
            getEdgesOfTree(node.getLeftChild(), node.getVertexId(), edgeList);
            // only add actual edges
            if (sourceId != 0) {
                EdgePair edge = new EdgePair(sourceId, node.getVertexId());
                edgeList.add(edge);
            }
            getEdgesOfTree(node.getRightChild(), node.getVertexId(), edgeList);
        }
    }

    /**
     * performs a recursive range search on the KD-Tree
     * @param query the range within which to search for points
     * @param node the current node being examined
     * @param depth the current depth in the KD-Tree, used to determine the axis of division
     * @return a set of Tuple2D points that fall within the specified range
     */
    public static Set<Tuple2D> rangeQuery(Range query, KDNode node, int depth) {
        Set<Tuple2D> result = new HashSet<>();
        // check if the node is null, return an empty set
        if (node == null) {
            return result;
        }
        // determine the relevant areas to search based on the current node's position and the axis of comparison
        int axis = depth % K; // determine the axis for comparison based on tree depth
        Tuple2D point = node.getLocation();

        // check if the current node's point is in the query range, add point to result
        if (query.contains(point)) {
            result.add(point);
        }

        // determine the relevant areas to search based on the current node's position and the axis of comparison
        Tuple2D leftTop = (axis == 0) ? new Tuple2D(node.getLocation().getX(), query.getUpperRight().getY()) : new Tuple2D(query.getUpperRight().getX(), node.getLocation().getY());
        Tuple2D rightBottom = (axis == 0) ? new Tuple2D(node.getLocation().getX(), query.getLowerLeft().getY()) : new Tuple2D(query.getLowerLeft().getX(), node.getLocation().getY());
        
        // create two ranges representing left and right children subspaces for further searching
        Range leftRange = new Range(query.getLowerLeft() , leftTop);
        Range rightRange = new Range(rightBottom, query.getUpperRight());
        // search the left child if the left range intersects with the query
        if (leftRange.intersects(query)) {
            result.addAll(rangeQuery(query, node.getLeftChild(), depth + 1));
        }
        // search the right child if the left range intersects with the query
        if (rightRange.intersects(query)) {
            result.addAll(rangeQuery(query, node.getRightChild(), depth + 1));
        }
        // return the set of points found within the query range
        return result; 
    }

    public static void main(String[] args) {
        // the filename of the file containing the points to be added to the KD-Tree
        String filename = "kd_tree_points.txt"; // name of the file containing points
        
        // read points from the file and store them in a list
        List<Tuple2D> points = readPointsFromFile(filename);
        
        // build the KD-Tree using the list of points 
        KDNode root = buildKDTree(points, 0);
        
        // uncomment the line below to print the KD-Tree structure to the console
        // printKDTree(root, 0, System.out);

        // define the range for the range query
        Range queryRange = new Range(new Tuple2D(10, 10), new Tuple2D(50, 35));

        // perform a range query on the KD-Tree and get the points within the specified range
        Set<Tuple2D> pointsInRange = KDTree.rangeQuery(queryRange, root, 0);

        // print the number of points found in the range
        System.out.println(pointsInRange.size());

        // iterate and print each point found in the range
        for (Tuple2D point : pointsInRange) {
            System.out.println(point);
        }
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
        String[] id = line.split(":");
        String[] parts = id[1].replaceAll("[()]", "").split(",\\s*");
        if (parts.length == 2) {
            try {
                // parse the x and y coordinates from the string parts
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                // return a new Tuple2D object with these coordinates
                return new Tuple2D(x, y);
            } catch (NumberFormatException e) {
                // handle any number format exceptions during parsing
                System.err.println("Invalid number format in line: " + line);
            }
        }
        // return null if the line cannot be parsed into a Tuple2D
        return null; 
    }
    /**
     * reads points from a specified file and converts them into a list of VertexTriplet objects
     * @param filename the name of the file containing the points
     * @return a list of VertexTriplet objects representing the points read from the file
     */
    public static List<VertexTriplet> readPointsFromFileTriplet(String filename) {
        List<VertexTriplet> points = new ArrayList<VertexTriplet>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // read each line from the file
            while ((line = reader.readLine()) != null) {
                // convert the line to a Tuple2D object
                VertexTriplet point = parseLineToTriplet(line);
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
    private static VertexTriplet parseLineToTriplet(String line) {
        // remove parentheses and split by comma
        String[] parts = line.replaceAll("[()]", "").split(",\\s*");
        String[] id = parts[0].split(":");
        if (parts.length == 2) {
            try {
                // parse the x and y coordinates from the string parts
                int x = Integer.parseInt(id[1].trim());
                int y = Integer.parseInt(parts[1].trim());
                long long_id = Long.parseLong(id[0].trim());
                // return a new VertexTriplet object with these coordinates and vertex id
                return new VertexTriplet(long_id, x, y);
            } catch (NumberFormatException e) {
                // handle any number format exceptions during parsing
                System.err.println("Invalid number format in line: " + line);
            }
        }
        // return null if the line cannot be parsed into a VertexTriplet
        return null; 
    }
}
