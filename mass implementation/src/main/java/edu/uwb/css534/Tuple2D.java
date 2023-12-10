package edu.uwb.css534;

import java.io.Serializable;

// import edu.uw.bothell.css.dsl.MASS.VertexPlace;

/**
 * the Tuple2D class represents a 2-dimensional point or coordinate in a 2D space
 * it encapsulates two integer values, x and y, which represent the point's coordinates
 */
public class Tuple2D  implements Serializable  {
    private static final long serialVersionUID = 1L; // Serial version UID for serialization.

    // the x and y coordinates of the point
    private int id, x, y;

    /**
     * default constructor
     * initializes a new Tuple2D object with id, x, and y all set to zero
     */
    public Tuple2D() {
        this.id = 0;
        this.x = 0; 
        this.y = 0;
    }

     /**
     * constructor with Object argument
     * @param arg The argument that could be used for initialization
     */
    public Tuple2D(Object arg) {
    }

    /**
     * constructs a new Tuple2D with specified x and y coordinates
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Tuple2D(int x, int y) {
        this.id = 0;
        this.x = x;
        this.y = y;
    }

    /**
     * constructs a new Tuple2D with specified id and,  x and y coordinates
     * @param id the id of the point
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    public Tuple2D(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * retrieves the id of this point
     * @return the point id
     */
    public int getId() {
        return id;
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
     * sets the id of this point
     * @param id the new point id
     */
    public void setId(int id) {
        this.id = id;
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
     * returns a string representation of this Tuple2D in the format "id:(x, y)"
     * @return a string representing the coordinates of this point
     */
    @Override
    public String toString() {
        return id+":(" + x + ", " + y + ")";
    }

     // overriding hashCode for consistent hashing based on id, x, and y
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    // overriding equals to compare Tuple2D objects based on id, x, and y
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple2D other = (Tuple2D) obj;
        if (id != other.id)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
