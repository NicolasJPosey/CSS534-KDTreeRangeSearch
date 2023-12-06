import org.apache.spark.SparkConf;                 // Spark Configuration
import org.apache.spark.api.java.JavaSparkContext; // Spark Context created from SparkConf
import org.apache.spark.api.java.JavaRDD;          // JavaRDD(T) created from SparkContext
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.EdgeTriplet;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;
import scala.Function1;
import scala.Function2;
import scala.runtime.AbstractFunction1;
import scala.runtime.AbstractFunction2;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import java.io.Serializable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;                           // Arrays, List, and Iterator returned from actions

public class RangeSearch {
  //Function definition for the edge predicate function
  //
  //The subgraph method only returns edges that include vertices that both satisfy the vertex predicate.
  //All of our edges have weight 1 so we just return true for the edges and allow the vertex predicate
  //to do the heavy lifting.
  public static Function1<EdgeTriplet<Tuple2D, Integer>, Object> EdgePredicate() {
    class NamelessClass extends AbstractFunction1<EdgeTriplet<Tuple2D, Integer>, Object> implements Serializable {
      public final Object apply(EdgeTriplet<Tuple2D, Integer> x) { return true; }
    }
    return new NamelessClass();
  }
  
  //Function definition for the vertex predicate function
  //
  //We desire vertices that are within our boundary x:[xMin, xMax] and y:[yMin, yMax].
  //Therefore, we return true if a vertex's location is between our bounds.
  public static Function2<Object, Tuple2D, Object> VertexPredicate(int xMin, int xMax, int yMin, int yMax) {
    class NamelessClass extends AbstractFunction2<Object, Tuple2D, Object> implements Serializable {
      public final Object apply(Object vid, Tuple2D attr) { return attr.getX() >= xMin && attr.getX() <= xMax && attr.getY() >= yMin && attr.getY() <= yMax; }
    }
    return new NamelessClass();
  }
  
  public static void main( String[] args ) { // a driver program
    //File containing points for constructing our KD tree
    String filename = args[0];
    //Number of points
    int numPoints = Integer.parseInt(args[1]);
    //Minimum x boundary
    int xMin = Integer.parseInt(args[2]);
    //Maximum x boundary
    int xMax = Integer.parseInt(args[3]);
    //Minimum y boundary
    int yMin = Integer.parseInt(args[4]);
    //Maximum y boundary
    int yMax = Integer.parseInt(args[5]);
    // initialize Spark Context
    SparkConf conf = new SparkConf( ).setAppName( "K-d Tree Range Search" );
    JavaSparkContext jsc = new JavaSparkContext( conf );

    //Read points into distributed system to build vertices
    JavaRDD<String> lines = jsc.textFile( filename );
    //Read points from file to build tree on master and get the edges
    List<VertexTriplet> points = KDTree.readPointsFromFileTriplet(filename);
    
    //Start timer
    long startTime = System.currentTimeMillis();
    
    //Build the tree
    KDNode root = KDTree.buildKDTreeFromTriplets(points, 0);
    //Get custom edges of the tree
    List<EdgePair> edgeList = new ArrayList<EdgePair>();
    KDTree.getEdgesOfTree(root, 0, edgeList);
    
    //Build vertices
    JavaRDD<Tuple2<Object, Tuple2D>> vertices = lines.flatMap( line -> {
        //List of tuple points
        List<Tuple2<Object, Tuple2D>> listOfPoints = new ArrayList<Tuple2<Object, Tuple2D>>();
        //Remove whitespace
        line.replace(" ","");
        //Get vertex id and vertex location
        String[] vertex = line.split(":");
        //Split on comma to get x and y
        String[] point = vertex[1].split(",");
        //Remove open parenthesis from x
        point[0] = point[0].replace("(","");
        //Remove close parenthesis from y
        point[1] = point[1].replace(")","");
        //Create tuple and return list
        listOfPoints.add(new Tuple2(Long.parseLong(vertex[0].trim()), new Tuple2D(Integer.parseInt(point[0].trim()), Integer.parseInt(point[1].trim()))));
        return listOfPoints.iterator();
    });
    
    //Create GraphX Edge objects in distributed system 
    JavaRDD<EdgePair> pairEdges = jsc.parallelize(edgeList);
    JavaRDD<Edge<Integer>> edges = pairEdges.flatMap( pair -> {
        List<Edge<Integer>> listOfEdges = new ArrayList<Edge<Integer>>();
        listOfEdges.add(new Edge<Integer>(pair.getSource(), pair.getDestination(), 1));
        return listOfEdges.iterator();
    });
    
    //Convert tree to GraphX graph object
    Graph<Tuple2D, Integer> myGraph = Graph.apply(vertices.rdd(), edges.rdd(), new Tuple2D(0,0), StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), ClassTag$.MODULE$.apply(Tuple2D.class), ClassTag$.MODULE$.apply(Integer.class));
    
    //Construct edge and vertex predicates for subgraph method
    Function1<EdgeTriplet<Tuple2D, Integer>, Object> EdgePred = new RangeSearch().EdgePredicate();
    Function2<Object, Tuple2D, Object> VertexPred = new RangeSearch().VertexPredicate(xMin, xMax, yMin, yMax);
    
    //Get subgraph of vertices that satisfy our range boundaries
    Graph<Tuple2D, Integer> rangeGraph = myGraph.subgraph(EdgePred, VertexPred);
    
    //Finish stop watch
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    
    //Collect the subgraph vertices into a local iterator for writing to file
    Iterator<Tuple2<Object, Tuple2D>> pointsInRange = rangeGraph.vertices().cache().toLocalIterator();
    scala.collection.immutable.List<Tuple2<Object, Tuple2D>> countPointsInRange = pointsInRange.toList();
    
    int count = countPointsInRange.size();
    pointsInRange = countPointsInRange.iterator();
    
    String outFile = "out-"+numPoints+".txt";
    //Write all the points in our range to a file to check correctness
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
        writer.write("Total number of points found: " + count);
        writer.newLine();
        while (pointsInRange.hasNext()) {
            //Get next point
            Tuple2<Object, Tuple2D> point = pointsInRange.next();
            //Format the point as a string and write it to the file
            String sPoint = String.format("(%d, %d)", point._2().getX(), point._2().getY());
            writer.write(sPoint);
            writer.newLine();
        }
        writer.write("Elapsed Time: " + elapsedTime);
        // print a confirmation message once all points are generated and saved
        System.out.println("Points generated and saved to " + outFile);
    } catch (IOException e) {
        // handle any io exceptions during file operations
        System.err.println("Error while writing to file: " + e.getMessage());
    }
    
    jsc.stop( ); // stop Spark Context
  }
}
