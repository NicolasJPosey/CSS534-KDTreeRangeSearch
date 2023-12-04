import java.io.Serializable;
import java.util.Comparator;

/**
 * Intermediary class for going from text vertex information to KD nodes
 */
class VertexTriplet implements Serializable {
    private long vertexId;
    private int x;
    private int y;
    
    public VertexTriplet(long vertexId, int x, int y) {
        this.vertexId = vertexId;
        this.x = x;
        this.y = y;
    }
    
    public long getVertexId() {
        return vertexId;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}
/**
 * Compares VertexTriplet using the x property
 */
class TripletComparatorX implements java.util.Comparator<VertexTriplet> {
    @Override
    public int compare(VertexTriplet a, VertexTriplet b) {
        return a.getX() - b.getX();
    }
}

/**
 * Compares VertexTriplet using the y property
 */
class TripletComparatorY implements java.util.Comparator<VertexTriplet> {
    @Override
    public int compare(VertexTriplet a, VertexTriplet b) {
        return a.getY() - b.getY();
    }
}