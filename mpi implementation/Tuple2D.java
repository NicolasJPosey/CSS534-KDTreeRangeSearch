

import java.io.Serializable;

/**
 * the Tuple2D class represents a 2-dimensional point or coordinate in a 2D space
 * it encapsulates two integer values, x and y, which represent the point's coordinates
 */
public class Tuple2D implements Serializable {
    // the x and y coordinates of the point
    private int x, y;

    /**
     * constructs a new Tuple2D with specified x and y coordinates
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Tuple2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * retrieves the x-coordinate of this point
     * @return the x-coordinate
     */
    public int getX(){
        return x;
    }
    /**
     * retrieves the y-coordinate of this point
     * @return the y-coordinate
     */
    public int getY(){
        return y;
    }

    /**
     * sets the x-coordinate of this point
     * @param x the new x-coordinate
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * sets the y-coordinate of this point
     * @param y the new y-coordinate
     */
    public void setY(int y){
        this.y = y;
    }
    
    /**
     * returns a string representation of this Tuple2D in the format "(x, y)"
     * @return a string representing the coordinates of this point
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}