

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mpi.MPI;


/** 
 * class representing a node in a KD-Tree
 */
class KDNode implements Serializable {
    // store point as a Tuple2D
    private Tuple2D location; 
    // left and right child nodes of the KDNode - representingt the subdivision of the space
    private KDNode leftChild, rightChild; 

    /**
     * constructor for KDNode
     * @param location The 2D point that this node will represent
     */
    public KDNode(Tuple2D location) {
        this.location = location;
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
class Range implements Serializable {
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
    public static KDNode buildKDTree(Tuple2D[] points, int depth) {
        // check if the list is empty, return null.
        if (points.length == 0) {
            return null;
        }
        // determine the axis for splitting (x-axis or y-axis)
        int axis = depth % K; 

        // sort the points based on the determined axis
        if (axis == 0) {
            Arrays.sort(points, Comparator.comparingInt(Tuple2D::getX));
        } else {
            Arrays.sort(points, Comparator.comparingInt(Tuple2D::getY));
        }

        // find the median index
        int medianIndex = points.length / 2; 
        // get the median point
        Tuple2D median = points[medianIndex];
        // create a new node with the median point
        KDNode node = new KDNode(median); 

        //build the left subtree using the left half of the points, increase depth by 1
        node.setLeftChild(buildKDTree(Arrays.copyOfRange(points, 0, medianIndex), depth + 1));
        //build the right subtree using the right half of the points, increase depth by 1
        node.setRightChild(buildKDTree(Arrays.copyOfRange(points, medianIndex + 1, points.length), depth + 1));

        return node; // return the root of the KD-Tree
    }

    /**
     * performs a recursive range search on the KD-Tree
     * @param query the range within which to search for points
     * @param node the current node being examined
     * @param depth the current depth in the KD-Tree, used to determine the axis of division
     * @return a set of Tuple2D points that fall within the specified range
     */
    public static List<Tuple2D> rangeQuery(Range query, KDNode node, int depth) {
        List<Tuple2D> result = new ArrayList();
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

    /**
     * reads points from a specified file and converts them into a list of Tuple2D objects
     * @param filename the name of the file containing the points
     * @return a list of Tuple2D objects representing the points read from the file
     */
    static List<Tuple2D> readPointsFromFile(String filename) {
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
     * parses a single line of text into a Tuple2D object
     * the line format is expected to be (x, y)
     * @param line A string representing a line in the file
     * @return A Tuple2D object if the line can be parsed, null otherwise
     */
    private static Tuple2D parseLineToTuple(String line) {
        // remove parentheses and split by comma
        String[] parts = line.split(":");
        parts = parts[1].replaceAll("[()]", "").split(",\\s*");
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

    public static void main(String[] args) {
        // check if the correct number of arguments are provided, if not, exit the program with a usage message
        if (args.length != 3) {
            System.out.println("Usage: java YourProgram <input_filename.txt> <x1:x2> <y1:y2>");
            System.exit(1);
        }
        // assigning the first argument as the filename of the input file
        String filename = args[0]; // name of the file containing points
        // declare a Range object to hold the query range
        Range queryRange ;

        try{
            // parsing command line arguments for x and y range, ranges are expected to be in the format x1:x2 and y1:y2
            String[] xRange = args[1].split(":");
            String[] yRange = args[2].split(":");

            // initializing the query range using the parsed values
            queryRange = new Range(new Tuple2D(Integer.parseInt(xRange[0]), Integer.parseInt(yRange[0])), new Tuple2D(Integer.parseInt(xRange[1]), Integer.parseInt(yRange[1])));

            // check if queryRange is not set
            if (queryRange == null){
                System.out.println("Error: queryRange is not set.");
                System.exit(1);
            }
            
            // read points from the file and store them in a list
            List<Tuple2D> pointsList = readPointsFromFile(filename);
            // convert the list of points to an array to send on mpi
            Tuple2D[] points = pointsList.toArray(new Tuple2D[0]);

            // start timer
            long startTime = System.currentTimeMillis();

            // initialize MPI
            MPI.Init(args);

            // get the rank (ID) of the current process and the total number of processes
            int rank = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();

            // calculate the number of points to be processed by each node
            int totalPoints = points.length; 
            int pointsPerNode = totalPoints / size;
            int remainingPoints = totalPoints % size;

            // arrays for holding the counts of points and their displacements for each process
            int[] sendCounts = new int[size];
            int[] displacements = new int[size];

            // populate the sendCounts and displacements arrays for data distribution among processes
            int currentDisplacement = 0;
            for (int i = 0; i < size; i++) {
                sendCounts[i] = (i < remainingPoints) ? pointsPerNode + 1 : pointsPerNode;
                displacements[i] = currentDisplacement;
                currentDisplacement += sendCounts[i];
            }
            
            // array for receiving the subset of points in each process
            Tuple2D[] localPoints = new Tuple2D[sendCounts[rank]];

             // distribute the points to each process using Scatterv
            MPI.COMM_WORLD.Scatterv(points, 0, sendCounts, displacements, MPI.OBJECT,
                                    localPoints, 0, sendCounts[rank], MPI.OBJECT, 0);

            // build the KD-Tree using the received subset of points
            KDNode root = buildKDTree(localPoints, 0);
            

            // perform a range query on the KD-Tree in each process and collect the results
            Tuple2D[] localResults = KDTree.rangeQuery(queryRange, root, 0).toArray(new Tuple2D[0]);
            int localCount = localResults.length;

            // initialize arrays on the root process for gathering results
            int[] allCounts = null;
            int[] allDisplacements = null;

            // initialize arrays on the root process for gathering results
            if (rank == 0) {
                allCounts = new int[size];
                allDisplacements = new int[size];
            }

            // Gather the counts of results from each process at the root
            MPI.COMM_WORLD.Gather(new int[]{localCount}, 0, 1, MPI.INT, 
                                allCounts, 0, 1, MPI.INT, 0);

            // calculate displacements and total size for the root rank
            // prepare an array on the root rank to receive all the data
            Tuple2D[] allResults = null;
            int totalSize = 0;
            if (rank == 0) {
                for (int i = 0; i < size; i++) {
                    allDisplacements[i] = totalSize;
                    totalSize += allCounts[i];
                }
                allResults = new Tuple2D[totalSize];
            }

            // // prepare an array on the root rank to receive all the data
            // Tuple2D[] allResults = null;
            // if (rank == 0) {
            //     allResults = new Tuple2D[totalSize];
            // }

            // collect all results at the root process using Gatherv
            MPI.COMM_WORLD.Gatherv(localResults, 0, localCount, MPI.OBJECT,
                                allResults, 0, allCounts, allDisplacements, MPI.OBJECT, 0);

            // process and output the results on the root process
            if (rank == 0) {
                // end timer
                long endTime = System.currentTimeMillis();
                // print the number of points found in the range
                System.out.println("Total number of points found: " + allResults.length);

                // print each point found in the range
                for (Tuple2D point : allResults) {
                    System.out.println(point);
                }
                // display the elapsed time
                System.out.println("Elapsed Time: " + (endTime - startTime));
            }
            // finalize the MPI environment
            MPI.Finalize( );

            
        } catch (NumberFormatException e) {
            System.out.println("Error: 2 and 3 arguments must be integers ranges <x1:x2> <y1:y2>.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: MPI error "+e.getMessage());
            System.exit(1);
        }
        
    }

}
