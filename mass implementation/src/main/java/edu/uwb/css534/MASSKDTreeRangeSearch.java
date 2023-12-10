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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.Date;
import java.util.List;


import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.GraphPlaces;
import edu.uw.bothell.css.dsl.MASS.MASS;

import edu.uw.bothell.css.dsl.MASS.SmartArgs2Agents;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

public class MASSKDTreeRangeSearch {

	// file path for MASS node configuration
	private static final String NODE_FILE = "nodes.xml"; 
	
	@SuppressWarnings("unused")		// some unused variables left behind for easy debugging
	public static void main( String[] args ) {
		// MASS graph to represent the KD-Tree
		GraphPlaces graph;
		
		
		// initialize MASS library with node file path and logging level
		MASS.setNodeFilePath( NODE_FILE );
		MASS.setLoggingLevel( LogLevel.DEBUG );

		// check if the correct number of command line arguments are provided
		if (args.length != 3) {
            MASS.getLogger().error("Usage: java YourProgram <input_filename.txt> <x1:x2> <y1:y2>");
        }
		
		// parse command line arguments
        String filename = args[0]; // Filename containing points for KD-Tree construction
        String[] xRange = args[1].split(":"); // x-range for search
        String[] yRange = args[2].split(":"); // y-range for search

		
		// define the range for the query
        int[] range = new int[]{Integer.parseInt(xRange[0]),Integer.parseInt(xRange[1]),Integer.parseInt(yRange[0]),Integer.parseInt(yRange[1])} ;  
		
		// read in points from the file
		List<Tuple2D> points = readPointsFromFile(filename);
		
		//starting timer
		long startTime = new Date().getTime();

		// initialize MASS.
		MASS.init();
		int gpHandle = 0;

		// name of the class representing places in MASS
		String placeClassName = KDNode.class.getName();

		// initialize GraphPlaces with KDNode
		graph = new GraphPlaces(gpHandle, placeClassName);
		MASS.setCurrentPlacesBase(graph); // fix getSize() null pointer issue

		// build the KD-Tree using the read points
		int rootId = buildKDTree( graph, points,0,-1);

		// initialize agents for search
		// name of the class representing agents in MASS
		String agentClassName = SearchAgent.class.getName();
		Object[] arg = new Object[2];
        arg[0] = range; // range
        arg[1] = 0;  // level on the tree
        SmartArgs2Agents arguments = new SmartArgs2Agents(SmartArgs2Agents.rangeSearch_, arg, -1, -1);
        
		// create one agent of Search agent 
		Agents searchers = new Agents(1, agentClassName, arguments, graph, 1);

		// place agents on the root of the KD-Tree
		searchers.callAll(SearchAgent.INIT_ON_ROOT,rootId);
		searchers.manageAll();

		// get number of agents
		int nAgents = searchers.nAgents();
		
		// initilize the list to collecet results 
		List<Tuple2D> results =  new ArrayList<>();

		// conduct the search and gather results
		while(nAgents != 0){
			// agnets perform search
			Object[] tempResults = (Object[]) searchers.callAll(SearchAgent.TREE_SEARCH,new Object[nAgents]);
			searchers.manageAll();

			// add results from agents to the results list
			for( int agent = 0 ; agent < tempResults.length ; agent++ ){
				if(tempResults[agent] != null){
					results.add((Tuple2D) tempResults[agent]);
				}
			}
			// get number of agents
			nAgents = searchers.nAgents();
		}

		// calculate execution time
		long execTime = new Date().getTime() - startTime;

		// orderly shutdown of MASS
		MASS.getLogger().debug( "KDTree instructs MASS library to finish operations..." );
		MASS.finish();
		MASS.getLogger().debug( "MASS library has stopped" );
		
		// print the found points
		for(Tuple2D point :results){
			System.out.println(point.toString());
		}
		
		// display execution time
		System.out.println( "Execution time = " + execTime + " milliseconds" );
		
	 }

	 /**
	  * method to build the KD-Tree using the provided points.
	  * @param graph graphPlaces object 
	  * @param points list of points 
	  * @param dimension the dimention of median split
	  * @param parentId the parent node id
	  * @return nodeId 
	 */
	 static int buildKDTree(GraphPlaces graph, List<Tuple2D> points, int dimension, int parentId) {
		// base case for recursion
		if (points.isEmpty()) return parentId;
		if (points.size() == 1) {
			Tuple2D singlePoint = points.get(0);
			int nodeId = graph.addVertexWithParams(singlePoint);
			return nodeId;
		}
	
		// Sort the points based on the current dimension
		Comparator<Tuple2D> comparator = (dimension == 0) ? Comparator.comparing(Tuple2D::getX) : Comparator.comparing(Tuple2D::getY);
		points.sort(comparator);
	
		// finding median
		int medianIndex = points.size() / 2;
		Tuple2D medianPoint = points.get(medianIndex);
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
			graph.getVertex(nodeId).left = leftChildId;
			//MASS.getLogger().debug("graph.getVertex(nodeId).left = " + graph.getVertex(nodeId).left );
			graph.addEdge(nodeId, leftChildId, 0);
		}

		if(nodeId != rightChildId) {
			graph.getVertex(nodeId).right = rightChildId;
			//MASS.getLogger().debug("graph.getVertex(nodeId).right = " + graph.getVertex(nodeId).right );
			graph.addEdge(nodeId, rightChildId, 0);
		}
	
		return nodeId;
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
