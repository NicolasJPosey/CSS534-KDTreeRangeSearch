package edu.uwb.css534;

import java.io.Serializable;
/**
 * Represents an edge in the tree from a source vertex to a destination vertex
 */
class EdgePair implements Serializable {
    private long source;
    private long destination;
    
    public EdgePair(long source, long destination) {
        this.source = source;
        this.destination = destination;
    }
    
    public long getSource() {
        return source;
    }
    
    public long getDestination() {
        return destination;
    }
}