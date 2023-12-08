package edu.uwb.css534;

import java.util.ArrayList;
import java.util.List;

import edu.uw.bothell.css.dsl.MASS.GraphAgent;
// import edu.uw.bothell.css.dsl.MASS.Agent;
import edu.uw.bothell.css.dsl.MASS.MASS;

// import edu.uw.bothell.css.dsl.MASS.SmartAgent;
import edu.uw.bothell.css.dsl.MASS.SmartArgs2Agents;

public class SearchAgent extends GraphAgent {

    public static final int INIT_ON_ROOT = 0;
    public static final int TREE_SEARCH = 1;

    Range searchRange;
    int[] range ;

    int k = 2;
   
    

    /**
     * This constructor will be called upon instantiation by MASS
     */
    public SearchAgent() {
        super();

    }

    /**
     * This constructor will be called upon instantiation by MASS
    //  */
    // public SearchAgent(Object args) {
    //     NodeArgs nodeArgs = (NodeArgs) args;
    //     searchRange = nodeArgs.getSearchRange();
    //     neighborIds = nodeArgs.getNeighborIds();
    //     dimension = nodeArgs.getDimension();
    //     foundPoints = new ArrayList<>();

    //     MASS.getLogger().debug("***** agent(" + getAgentId() + ") was created. " + nodeArgs.toString());



    // }

     public SearchAgent(Object args) {
        SmartArgs2Agents attr = (SmartArgs2Agents) args;
        range =  attr.searchRange;
        searchRange = new Range(new Tuple2D(range[0], range[2]), new Tuple2D(range[1], range[3]));
        MASS.getLogger().debug("***** agent(" + getAgentId() + ") was created. ") ;

    }

    /**
     * This method is called when "callAll" is invoked from the master node
     */
    public Object callMethod(int method, Object o) {
        switch (method) {
            case INIT_ON_ROOT:
                return initOnRoot(o);
            case TREE_SEARCH:
                return searchTree();
            default:
                return new String("Unknown Method Number: " + method);

        }

    }

    /**
     * Move this Agent to root node of KDTree
     * 
     * @param rootId
     * @return True if migration is successfull
     */
    public boolean initOnRoot(Object root) {
        
        int rootId = (int) root;
        MASS.getLogger().error("***** agent(" + getAgentId() + ") migrate to: "+rootId );
        
       
        //  MASS.getLogger().debug("***** agent(" + getAgentId() + ") migrate to: "+ rootArgs.getRootId() + " MASS.distributed_map " + MASS.distributed_map.get(rootArgs.getRootId()) + ".");
        return migrate(rootId);
    }

    // public Object searchTree() {

    //     Tuple2D point = (Tuple2D) getPlace();
    //     // chech if the point is with in the search range and not yet added
    //     if (!(getPlace().getVisited()) && searchRange.contains(point)) {
            
    //         getPlace().setVisited(true);
    //         return point;
    //     }
    //     // get place neighbors 
    //     Object[] neighbors = ((KDNode) getPlace()).getNeighbors();
    //     // if leaf: kill agent and returen results
    //     if (neighbors.length == 0) {
    //         kill();
    //         MASS.getLogger().debug("***** agent(" + getAgentId() + ") was killed.");
    //         return (Object[]) (foundPoints.toArray(new Tuple2D[0]));
    //     }
    //     // chech if agent was not spawned in this place (doesn't have neighborIds)
    //     if (neighborIds.length == 0) {
    //         neighborIds = new int[neighbors.length];
    //         // iterate over the neighbors and unbox each element ad add them to neighborIds list
    //         for (int i = 0; i < neighbors.length; i++) {
    //             neighborIds[i] = (Integer) neighbors[i]; // auto-unboxing from object to int
    //         }
    //         // chech if node has 2 neighbors (left and right)
    //         if (neighbors.length == 2) {
    //             // spawn agent to search 2nd neighbor 
    //             NodeArgs nodeArgs = new NodeArgs(searchRange, new int[] { neighborIds[1] }, dimension);
    //             spawn(1, new Object[] { nodeArgs });
    //             MASS.getLogger().debug("***** agent(" + getAgentId() + ") was spawnd an agnet at dimension " + dimension + ".");
    //             // migrate to 1sh neighbor 
    //             migrate(neighborIds[0]);
    //             MASS.getLogger().debug("***** agent(" + getAgentId() + ") migrate to: " + neighborIds[0] + ".");

    //         } else { // node has 1 neighbors  mig
    //             migrate(neighborIds[0]);
    //             MASS.getLogger().debug("***** agent(" + getAgentId() + ") migrate to: " + neighborIds[0] + ".");
    //         }
    //         dimension = dimension == 0 ? 1 : 0;
    //     } else { // agent was spawned (has neighborIds): migrate to neighborId[0]
    //         migrate(neighborIds[0]);
    //         MASS.getLogger().debug("***** agent(" + getAgentId() + ") migrate to: " + neighborIds[0] + ".");
    //     }
    //     // reset neighborIds 
    //     neighborIds = new int[0];
    //     return null;
    // }

    public Object searchTree() {

        Tuple2D point = (Tuple2D) getPlace();

        int axis = level % k;
        // determine the relevant areas to search based on the current node's position and the axis of comparison
        Tuple2D leftTop = (axis == 0) ? new Tuple2D(point.getX(), searchRange.getUpperRight().getY()) : new Tuple2D(searchRange.getUpperRight().getX(), point.getY());
        Tuple2D rightBottom = (axis == 0) ? new Tuple2D(point.getX(), searchRange.getLowerLeft().getY()) : new Tuple2D(searchRange.getLowerLeft().getX(), point.getY());
        MASS.getLogger().debug( " point : " + point.toString());

        level ++;
        // create two ranges representing left and right children subspaces for further searching
        Range leftRange = new Range(searchRange.getLowerLeft() , leftTop);
        Range rightRange = new Range(rightBottom, searchRange.getUpperRight());


        // search the left child if the left range intersects with the query
        propagateTree(BothBranch_, range);
        // if (leftRange.intersects(searchRange) && rightRange.intersects(searchRange)) {
        //     // result.addAll(rangeQuery(searchRange, node.getLeftChild(), level));

        //     // BothBranch_
        //     propagateTree(BothBranch_, range);
        // }else{
        // // search the right child if the left range intersects with the query
        //     if (rightRange.intersects(searchRange)) {
        //     // result.addAll(rangeQuery(searchRange, node.getRightChild(), level));
        //     // RightBranch_ 
        //     propagateTree(RightBranch_,range);
        //     }
        //     if (leftRange.intersects(searchRange)) {
        //     // result.addAll(rangeQuery(searchRange, node.getRightChild(), level));
        //     // LeftBranch_
        //     propagateTree(LeftBranch_, range);
        //     }
        // }

        if (!(getPlace().getVisited()) && searchRange.contains(point)) {
            getPlace().setVisited(true);
            MASS.getLogger().debug( " point : " + point.toString());
            return point;
        }
        return null;
    }
       

}
