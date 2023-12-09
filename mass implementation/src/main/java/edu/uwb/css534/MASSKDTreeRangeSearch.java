/*

 	MASS Java Software License
	© 2012-2015 University of Washington

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	The following acknowledgment shall be used where appropriate in publications, presentations, etc.:      

	© 2012-2015 University of Washington. MASS was developed by Computing and Software Systems at University of 
	Washington Bothell.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.

*/

package edu.uwb.css534;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.GraphPlaces;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Place;
import edu.uw.bothell.css.dsl.MASS.SmartArgs2Agents;
import edu.uw.bothell.css.dsl.MASS.VertexPlace;
import edu.uw.bothell.css.dsl.MASS.graph.transport.VertexModel;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

public class MASSKDTreeRangeSearch {

	private static final String NODE_FILE = "nodes.xml";

	// public static GraphPlaces graph;
	
	@SuppressWarnings("unused")		// some unused variables left behind for easy debugging
	public static void main( String[] args ) {
		GraphPlaces graph;
		
		
		// init MASS library
		MASS.setNodeFilePath( NODE_FILE );
		MASS.setLoggingLevel( LogLevel.DEBUG );

		if (args.length != 3) {
            MASS.getLogger().error("Usage: java YourProgram <input_filename.txt> <x1:x2> <y1:y2>");
        }
		
		//File containing points for constructing our KD tree
		String filename = args[0];

		// parsing command line arguments for x and y search range
		String[] xRange = args[1].split(":");
		String[] yRange = args[2].split(":");
		
		// define the range for the range query
		// Range queryRange = new Range(new Tuple2D(Integer.parseInt(xRange[0]), Integer.parseInt(yRange[0])), new Tuple2D(Integer.parseInt(xRange[1]), Integer.parseInt(yRange[1])));
        int[] range = new int[]{Integer.parseInt(xRange[0]),Integer.parseInt(xRange[1]),Integer.parseInt(yRange[0]),Integer.parseInt(yRange[1])} ;  
		//Read in points
		List<Tuple2D> points = KDTree.readPointsFromFile(filename);
		// List<VertexTriplet> points = KDTree.readPointsFromFileTriplet(filename);
		// remember starting time
		
		
		long startTime = new Date().getTime();
		//Build the tree
		// KDNode root = KDTree.buildKDTreeFromTriplets(points, 0);
		// //Get custom edges of the tree
		// List<EdgePair> edgeList = new ArrayList<EdgePair>();
		// KDTree.getEdgesOfTree(root, 0, edgeList);
		
		MASS.init( );
		int gpHandle = 0;
		//Not sure if this is the class name that we actually need to pass into GraphPlaces
		String placeClassName = KDNode.class.getName();
		graph = new GraphPlaces(gpHandle, placeClassName);
		MASS.setCurrentPlacesBase(graph);

		
		

		// Map<Integer,Integer> vertexIdMap =  new HashMap<>();
		//Add vertices to graph
		// for(VertexTriplet vertex : points) {
			
		//     int id = graph.addVertex(vertex.getVertexId(),new Tuple2D(vertex.getX(), vertex.getY()));
		// 	vertexIdMap.put((int) vertex.getVertexId(), graph.addVertex(vertex.getVertexId(),new Tuple2D(vertex.getX(), vertex.getY())));
		// 	MASS.getLogger().debug( " --- vertex.getVertexId() = " + vertex.getVertexId()  + " id = " + id+" (x,y) = ("+ vertex.getX()+","+vertex.getY() +")" );
		// }
		//Add edges to graph
		// for(EdgePair edge : edgeList) {
		// MASS.getLogger().debug( " --- vertexIdMap.get((int)edge.getSource()) = " + vertexIdMap.get((int)edge.getSource())  + " vertexIdMap.get((int)edge.getDestination()) = " + vertexIdMap.get((int)edge.getDestination()));
		// graph.addEdge(vertexIdMap.get((int)edge.getSource()), vertexIdMap.get((int)edge.getDestination()), 1.0);
		// }

		int rootId = buildKDTree( graph, points,0,-1);

		MASS.getLogger().debug(" graph.getPlaces().toString() = " +graph.getPlaces().toString() );
		MASS.getLogger().debug(" graph.size() = " +graph.size() );
		String agentClassName = SearchAgent.class.getName();
		
		for (VertexModel point :  graph.getGraph().getVertices()){
			// Tuple2D place = (Tuple2D) point ;
			// MASS.getLogger().debug("point: "+ place.left + " - " + place.right + " id:(x,y) =  " + place.getId()+":("+place.getX() +","+place.getY() +")");
			// MASS.getLogger().debug("place.getNeighbors(): "+place.getNeighbors().toString());
			MASS.getLogger().debug("place.getNeighbors(): "+point.id +" - "+point.neighbors.size());
			
			
		}
		MASS.getLogger().debug("graph.getVertex("+rootId+"): "+graph.getVertex(rootId).neighbors+" - "+graph.getVertex(rootId).neighbors.size());
		 
		MASS.getLogger().debug("graph.getVertex(rootId): "+graph.getVertex(rootId).left+" - "+ graph.getVertex(rootId).right );
		KDNode n = (KDNode) graph.getVertex(rootId);
		MASS.getLogger().debug( " v " + " id:(x,y) =  " + n.location.getId()+":("+n.location.getX() +","+n.location.getY() +")");
		// NodeArgs nodeArgs = new NodeArgs(rootId, queryRange, 0);
		Object[] arg = new Object[2];
        arg[0] = range; //Range
        arg[1] = 0;  //Level on the tree
        SmartArgs2Agents arguments = new SmartArgs2Agents(SmartArgs2Agents.rangeSearch_, arg, -1, -1);
        Agents searchers = new Agents(1, agentClassName, arguments, graph, 1);

		
		
		// Agents searchers = new Agents(2, agentClassName, nodeArgs, graph, 1);

		searchers.callAll(SearchAgent.INIT_ON_ROOT,rootId);
		searchers.manageAll();

		int nAgents = searchers.nAgents();
		// List<List<Tuple2D>> results =  new ArrayList<>();
		List<Tuple2D> results =  new ArrayList<>();
		
		while(nAgents != 0){
			MASS.getLogger().debug( "in while" );

			Object[] tempResults = (Object[]) searchers.callAll(SearchAgent.TREE_SEARCH,new Object[nAgents]);
			searchers.manageAll();
			MASS.getLogger().debug( "Quickstart "+tempResults +" - "+tempResults.length );


			for( int agent = 0 ; agent < tempResults.length ; agent++ ){
				if(tempResults[agent] != null){
					results.add((Tuple2D) tempResults[agent]);
				}
			}
			nAgents = searchers.nAgents();
		}
		// calculate / display execution time
		long execTime = new Date().getTime() - startTime;
		// orderly shutdown
		MASS.getLogger().debug( "Quickstart instructs MASS library to finish operations..." );
		MASS.finish();
		MASS.getLogger().debug( "MASS library has stopped" );
		

		// for(List<Tuple2D> list: results){
			for(Tuple2D point :results){
				System.out.println(point.toString());
			}
		// }

		System.out.println( "Execution time = " + execTime + " milliseconds" );
		
	 }

	 

	 static int buildKDTree(GraphPlaces graph, List<Tuple2D> points, int dimension, int parentId) {
		// base case for recursion
		if (points.isEmpty()) return parentId;
		if (points.size() == 1) {
			Tuple2D singlePoint = points.get(0);
			// int nodeId = graph.addVertex(singlePoint.getId(), singlePoint);
			int nodeId = graph.addVertexWithParams(singlePoint);
			return nodeId;
		}
	
		// Sort the points based on the current dimension
		// Comparator<Tuple2D> comparator = (dimension == 0) ?  Comparator.comparingInt(t -> t.point[0]):  Comparator.comparingInt(t -> t.point[1]);
		Comparator<Tuple2D> comparator = (dimension == 0) ? Comparator.comparing(Tuple2D::getX) : Comparator.comparing(Tuple2D::getY);
		points.sort(comparator);
	
		// finding median
		int medianIndex = points.size() / 2;
		Tuple2D medianPoint = points.get(medianIndex);
		// int nodeId = graph.addVertex(medianPoint.getId(), medianPoint);
		int nodeId = graph.addVertexWithParams(medianPoint);
		
	
		// split the list for left and right subtrees
		List<Tuple2D> leftPoints = new ArrayList<>(points.subList(0, medianIndex));
		List<Tuple2D> rightPoints = new ArrayList<>(points.subList(medianIndex + 1, points.size()));
	
		// alternate dimension for the next level
		int nextDimension = 1 - dimension;
	
		// recursive calls for left and right subtrees
		int leftChildId = buildKDTree(graph,leftPoints, nextDimension, nodeId);
		int rightChildId = buildKDTree(graph,rightPoints, nextDimension, nodeId);
	
		// add edges to the graph
		if(nodeId != leftChildId){ 
			// medianPoint.left = leftChildId;
			graph.getVertex(nodeId).left = leftChildId;
			// MASS.getLogger().debug("medianPoint.left= " +medianPoint.left );
			MASS.getLogger().debug("graph.getVertex(nodeId).left = " + graph.getVertex(nodeId).left );
			graph.addEdge(nodeId, leftChildId, 0);
		}

		if(nodeId != rightChildId) {
			graph.getVertex(nodeId).right = rightChildId;
			// medianPoint.right = rightChildId;
			// MASS.getLogger().debug("medianPoint.right= " +medianPoint.right );
			MASS.getLogger().debug("graph.getVertex(nodeId).right = " + graph.getVertex(nodeId).right );
			graph.addEdge(nodeId, rightChildId, 0);
		}
	
		return nodeId;
	}
	
	 
}
