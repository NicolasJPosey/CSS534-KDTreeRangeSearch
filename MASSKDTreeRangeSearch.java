import java.util.List;
import MASS.*;

public class GraphCreation {
  public void main(String[] args) {
    //File containing points for constructing our KD tree
    String filename = args[0];
    //Read in points
    List<VertexTriplet> points = KDTree.readPointsFromFileTriplet(filename);
    //Build the tree
    KDNode root = KDTree.buildKDTreeFromTriplets(points, 0);
    //Get custom edges of the tree
    List<EdgePair> edgeList = new ArrayList<EdgePair>();
    KDTree.getEdgesOfTree(root, 0, edgeList);
    
    MASS.init( );
    int gpHandle = 0;
    //Not sure if this is the class name that we actually need to pass into GraphPlaces
    String className = Tuple2D.class.getName();
    GraphPlaces graph = new GraphPlaces(gpHandle, className);
    
    //Add vertices to graph
    for(VertexTriplet vertex : points) {
      graph.addVertex(vertex.getVertexId(), new Tuple2D(vertex.getX(), vertex.getY()));
    }
    //Add edges to graph
    for(EdgePair edge : edgeList) {
      graph.addEdge(edge.getSource(), edge.getDestination(), 1.0);
    }
    
    int updateStateFuncID = 0;
    graph.callAll(updateStateFuncID);
    int forwardMsgFuncID = 1;
    graph.exchangeAll(forwardMsgFuncID);
    Agents searcher = new Agents(2, "Motif", graph, 101);
    crawler.doWhile(()->crawlers.hasAgents());
    MASS.finish();
  }
}

public class Motif extends Agent {
  @onCreation public void init( ) { ...; }
  @onArrival public void walk( ) { ...; }
}