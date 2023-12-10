package edu.uwb.css534;

import edu.uw.bothell.css.dsl.MASS.GraphAgent;

import edu.uw.bothell.css.dsl.MASS.MASS;

import edu.uw.bothell.css.dsl.MASS.SmartArgs2Agents;

// SearchAgent extends GraphAgent and is responsible for searching within a KDTree
public class SearchAgent extends GraphAgent {

    // constants for identifying methods in callMethod
    public static final int INIT_ON_ROOT = 0;
    public static final int TREE_SEARCH = 1;

    // Range object to hold the search range
    Range searchRange; 
     // array to hold range values
    int[] range; 

    // dimensionality of the space, here it is 2 for 2D
    int k = 2;

    /**
     * default constructor called by MASS framework during instantiation
     */
    public SearchAgent() {
        super();
    }

    /**
     * constructor with arguments for initializing the search range
     * @param args arguments received from the MASS framework
     */
    public SearchAgent(Object args) {
        super(args);
        SmartArgs2Agents attr = (SmartArgs2Agents) args;
        range = attr.searchRange;
        searchRange = new Range(new Tuple2D(range[0], range[2]), new Tuple2D(range[1], range[3]));
        MASS.getLogger().debug("***** agent(" + getAgentId() + ") was created. ");
    }

    /**
     * method called when "callAll" is invoked from the master node
     * It executes different functions based on the method identifier
     * @param method rhe method identifier
     * @param o additional object parameter
     * @return eesult of the invoked method
     */
    public Object callMethod(int method, Object o) {
        switch (method) { 
            case INIT_ON_ROOT: // moves this agent to the root node of KDTree
                return initOnRoot(o);
            case TREE_SEARCH: // conducts a search within the KDTree
                return searchTree();
            default:
                return new String("Unknown Method Number: " + method);

        }

    }

   /**
     * moves this agent to the root node of KDTree
     * @param root The root node identifier
     * @return True if migration is successful
     */
    public boolean initOnRoot(Object root) {

        int rootId = (int) root;
        MASS.getLogger().error("***** agent(" + getAgentId() + ") migrate to: " + rootId);
        return migrate(rootId);
    }
    
     /**
     * conducts a search within the KDTree
     * determines relevant areas to search based on the node's position and axis of comparison
     * @return the location of the node if it's within the search range and hasn't been visited
     */
    public Object searchTree() {
        // get KDNode of the agent
        KDNode node = (KDNode) getPlace();

        // determine the axis of comparison
        int axis = level % k;

        // calculate leftTop and rightBottom points for dividing the search area
        Tuple2D leftTop = (axis == 0) ? new Tuple2D(node.location.getX(), searchRange.getUpperRight().getY())
                : new Tuple2D(searchRange.getUpperRight().getX(), node.location.getY());
        Tuple2D rightBottom = (axis == 0) ? new Tuple2D(node.location.getX(), searchRange.getLowerLeft().getY())
                : new Tuple2D(searchRange.getLowerLeft().getX(), node.location.getY());

        // increment tree level for the agent
        level++;

        // create two ranges representing left and right children subspaces for further searching
        Range leftRange = new Range(searchRange.getLowerLeft(), leftTop);
        Range rightRange = new Range(rightBottom, searchRange.getUpperRight());

        // propagate search to child nodes based on intersection with search range
        if (leftRange.intersects(searchRange) && rightRange.intersects(searchRange)) {
            // BothBranch_
            propagateTree(BothBranch_, range);
        } else {
            // search the right child if the right range intersects with the query
            if (rightRange.intersects(searchRange)) {
                // RightBranch_
                propagateTree(RightBranch_, range);
            }
            // search the left child if the left range intersects with the query
            if (leftRange.intersects(searchRange)) {
                // LeftBranch_
                propagateTree(LeftBranch_, range);
            }
        }
         // check if the current place has not been visited and contains the node's location
        if (!(getPlace().getVisited()) && searchRange.contains(node.location)) {
            getPlace().setVisited(true);
            MASS.getLogger().debug("***** found point : " + node.location.toString());
            return node.location;
        }
        // return null if no point is found
        return null;
    }

}
