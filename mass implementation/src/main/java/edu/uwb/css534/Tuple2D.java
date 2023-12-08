package edu.uwb.css534;

import edu.uw.bothell.css.dsl.MASS.VertexPlace;

/**
 * the Tuple2D class represents a 2-dimensional point or coordinate in a 2D space
 * it encapsulates two integer values, x and y, which represent the point's coordinates
 */
public class Tuple2D  extends VertexPlace  {
    private static final long serialVersionUID = 1L;
    // the x and y coordinates of the point
     int[] point ;

    /**
     * constructs a new Tuple2D with specified x and y coordinates
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */

    public Tuple2D() {
       super();
       int[] point = new int[3];
    }
    public Tuple2D(Object obj) {
        super();
        int[] point = (int[]) obj;
        
        


     }

    public Tuple2D(int x, int y) {
        int[] point = new int[3];
        point [0 ] = x;
        point[1] = y;
       
    }

     public Tuple2D(int id, int x, int y ) {
        int[] point = new int[3];
        point [0 ] = x;
        point[1] = y;
        point[2] = y;
    }

    /**
     * gets the x-coordinate of this point
     * @return the x-coordinate
     */
    public int getX(){
        return point[0];
    }
    /**
     * gets the y-coordinate of this point
     * @return the y-coordinate
     */
    public int getY(){
        return point[1];
    }

    /**
     * sets the x-coordinate of this point
     * @param x the new x-coordinate
     */
    public void setX(int x){
        this.point[0] = x;
    }

    /**
     * sets the y-coordinate of this point
     * @param y the new y-coordinate
     */
    public void setY(int y){
        this.point[1] = y;
    }
    
    /**
     * returns a string representation of this Tuple2D in the format "(x, y)"
     * @return a string representing the coordinates of this point
     */
    @Override
    public String toString() {
        return point[2]+":(" + point[0] + ", " + point[1] + ")";
    }
    
    /**
     * gets the id of this point
     * @return the point id 
     */
    public int getId() {
        return point[2];
    }

    /**
     * sets the id of this point
     * @param id the new point id
     */
    public void setId(int id) {
        this.point[2] = id;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + point[0];
        result = prime * result + point[1];
        result = prime * result + point[2];
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
        Tuple2D other = (Tuple2D) obj;
        if (point[0] != other.point[0])
            return false;
        if (point[1] != other.point[1])
            return false;
        if (point[2] != other.point[2])
            return false;
        return true;
    }
}
