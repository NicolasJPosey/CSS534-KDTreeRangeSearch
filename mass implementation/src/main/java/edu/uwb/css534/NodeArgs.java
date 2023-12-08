package edu.uwb.css534;

import java.io.Serializable;
import java.util.Arrays;

public class NodeArgs implements Serializable {

    int rootId;
    Range searchRange;
    int dimension;
    int[] neighborIds;

     public NodeArgs(){
        super();
     }

    public NodeArgs(int rootId, Range searchRange, int dimension){
        this.rootId = rootId;
        this.searchRange = searchRange;
        this.dimension = dimension;

    }
    public NodeArgs ( Range searchRange, int[] neighborIds, int dimension){
        this.searchRange = searchRange;
        this.neighborIds = neighborIds;
        this.dimension = dimension;
    }
    public Range getSearchRange() {
        return searchRange;
    }
    public void setSearchRange(Range searchRange) {
        this.searchRange = searchRange;
    }
    public int getDimension() {
        return dimension;
    }
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    public int[] getNeighborIds() {
        return neighborIds;
    }
    public void setNeighborIds(int[] neighborIds) {
        this.neighborIds = neighborIds;
    }
    public int getRootId() {
        return rootId;
    }
    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    @Override
    public String toString() {
        return "NodeArgs [rootId=" + rootId + ", searchRange=" + searchRange + ", dimension=" + dimension
                + ", neighborIds=" + Arrays.toString(neighborIds) + "]";
    }
    

}
