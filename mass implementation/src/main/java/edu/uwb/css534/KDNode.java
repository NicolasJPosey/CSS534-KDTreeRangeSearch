package edu.uwb.css534;

import edu.uw.bothell.css.dsl.MASS.VertexPlace;

class KDNode  extends VertexPlace {
    // label for node
    private int nodeId;
    // store point as a Tuple2D
    private Tuple2D location; 


    /**
     * constructor for KDNode
     */
    public KDNode() {
        super();
    }
    
     /**
     * constructor for KDNode
     * @param location The 2D point that this node will represent
     */
    public KDNode(Tuple2D location) {
        super();
        this.nodeId = 0;
        this.location = location;
    }

    /**
     * constructor for KDNode
     * @param location The 2D point that this node will represent
     */
    public KDNode(Object arg) {
        super();
        this.nodeId = 0;
        this.location = (Tuple2D) arg;
    }

    /**
     * constructor for KDNode
     * @param nodeInfo Information about the node such as it's ID and location that the node will represent
     */
    public KDNode( int nodeId , Tuple2D location) {
        super();
        this.nodeId = nodeId;
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
     * retrieves the vertexId of this node
     * @return the vertex id for the node
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * sets the vertexId of this node
     * @param vertexId the new vertexId for this node
     */
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeId;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KDNode other = (KDNode) obj;
        if (nodeId != other.nodeId)
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        return true;
    }
    

}
