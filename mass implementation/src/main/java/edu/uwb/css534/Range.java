package edu.uwb.css534;

import java.io.Serializable;

public class  Range  implements Serializable  {
    // lower-left corner of the range - representing the minimum x and y values
    private final Tuple2D lowerLeft; 
    // upper-right corner of the range - representing the maximum x and y values
    private final Tuple2D upperRight;
    
    /**
     * constructor for the Range class.
     * @param lowerLeft the lower-left corner of the range
     * @param upperRight the upper-right corner of the range
     */
    public Range(Tuple2D lowerLeft, Tuple2D upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }
    /**
     * checks if the range contains a given point
     * @param point the point to check
     * @return true if the point is within the range, false otherwise
     */
    public boolean contains(Tuple2D point) {
        return point.getX() >= lowerLeft.getX() && point.getX() <= upperRight.getX() &&
               point.getY() >= lowerLeft.getY() && point.getY() <= upperRight.getY();
    }
    /**
     * checks if this range intersects with another given range
     * @param searchRegion the range to check for intersection with
     * @return true if the ranges intersect, false otherwise
     */
    public boolean intersects(Range searchRegion ) {
        return this.lowerLeft.getX() <= searchRegion.upperRight.getX() &&
               this.upperRight.getX() >= searchRegion.lowerLeft.getX() &&
               this.lowerLeft.getY() <= searchRegion.upperRight.getY() &&
               this.upperRight.getY() >= searchRegion.lowerLeft.getY();
    }

    /**
     * retrieves the lower-left corner of the range
     * @return the lower-left corner as Tuple2D
     */
    public Tuple2D getLowerLeft() {
        return lowerLeft;
    }

    /**
     * retrieves the upper-right corner of the range
     * @return the upper-right corner as Tuple2D
     */
    public Tuple2D getUpperRight() {
        return upperRight;
    }
}