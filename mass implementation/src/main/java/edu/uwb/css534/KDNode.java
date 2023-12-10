package edu.uwb.css534;

import java.io.Serializable;

import edu.uw.bothell.css.dsl.MASS.VertexPlace;


// KDNode class extends VertexPlace and implements Serializable interface
// This class is used to represent a node in a KD-Tree
public class KDNode  extends VertexPlace  implements Serializable {
    // id for node
    int nodeId;
    // store point as a Tuple2D
    Tuple2D location; 


    /**
     * constructor for KDNode with a single argument
     * initializes a KDNode with a given location
     * @param arg the 2D point (Tuple2D) that this node will represent
     */
    public KDNode(Object arg) {
        super();
        this.nodeId = 0;
        this.location = (Tuple2D) arg;
    }


    /**
     * constructor for KDNode with nodeId and location
     * initializes a KDNode with specified nodeId and location
     * @param nodeId the unique identifier for the node
     * @param location the 2D point (Tuple2D) that the node will represent
     */
    public KDNode( int nodeId , Tuple2D location) {
        // super();
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
     * retrieves the nodeId of this node
     * @return the vertex id for the node
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * sets the vertexId of this node
     * @param nodeId the new nodeId for this node
     */
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    // overriding hashCode method for consistent hashing based on nodeId and location
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeId;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        return result;
    }
    
    // overriding equals method to compare KDNode objects based on nodeId and location
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
