package ca.mcgill.cs.comp409.a4.q1.grid.items;

import ca.mcgill.cs.comp409.a4.q1.grid.util.GridPoint2D;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TileItem implements Comparable<TileItem> {

    private static int aNextId = 1;
    private int aId;

    private GridItem aItem;

    private GridPoint2D aCoordinates;
    private Lock aLock;
    public TileItem(int x, int y) {
        aCoordinates = new GridPoint2D(x, y);
        aId = aNextId++;
        aLock = new ReentrantLock();
    }

    /**
     * Getter method for tile's grid item
     * @return Tile's grid item
     */
    public GridItem getItem() {
        return aItem;
    }

    /**
     * Setter method for tile's grid item
     * @param pItem Tile's new grid item
     */
    void setItem(GridItem pItem) {
        aItem = pItem;
    }

    /**
     * Removes the tile's current item by setting it to null
     */
    void removeItem() {
        aItem = null;
    }

    /**
     * Locks the tile's re-entrant lock
     */
    void acquire() {
        aLock.lock();
    }

    /**
     * Releases the tile's re-entrant lock
     */
    void release() {
        aLock.unlock();
    }

    /**
     * Checks if the tile's item is null, signifying tile is free to move to by a character
     * @return True if tile has no grid item, false otherwise
     */
    boolean isFree() {
        return aItem == null;
    }

    /**
     * Getter method for the coordinates of the tile item
     * @return Coordinates of the tile item
     */
    public GridPoint2D getCoordinates() {
        return aCoordinates;
    }

    @Override
    public int compareTo(TileItem o) {
        return Integer.compare(this.aId, o.aId);
    }

    /**
     * Getter method for the x coordinates of the tile item
     * @return X coordinates of the tile item
     */
    public int getX() {
        return aCoordinates.getX();
    }

    /**
     * Getter method for the y coordinates of the tile item
     * @return Y coordinates of the tile item
     */
    public int getY() {
        return aCoordinates.getY();
    }
}
