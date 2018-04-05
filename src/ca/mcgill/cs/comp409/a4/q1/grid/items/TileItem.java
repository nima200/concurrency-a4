package ca.mcgill.cs.comp409.a4.q1.grid.items;

import ca.mcgill.cs.comp409.a4.q1.grid.util.GridPoint2D;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TileItem implements Comparable<TileItem> {

    private static int aNextId = 1;
    private int aId;

    private volatile GridItem aItem;

    private GridPoint2D aCoordinates;
    private Lock aLock;
    public TileItem(int x, int y) {
        aCoordinates = new GridPoint2D(x, y);
        aId = aNextId++;
        aLock = new ReentrantLock();
    }

    public TileItem(GridItem pItem, int x, int y) {
        aItem = pItem;
        aCoordinates = new GridPoint2D(x, y);
    }

    public GridItem getItem() {
        return aItem;
    }

    public void setItem(GridItem pItem) {
        aItem = pItem;
    }

    public void removeItem() {
        aItem = null;
    }

    public void acquire() {
        aLock.lock();
    }

    public void release() {
        aLock.unlock();
    }

    public boolean isFree() {
        return aItem == null;
    }

    public GridPoint2D getCoordinates() {
        return aCoordinates;
    }

    @Override
    public int compareTo(TileItem o) {
        return Integer.compare(this.aId, o.aId);
    }

    public int getX() {
        return aCoordinates.getX();
    }

    public int getY() {
        return aCoordinates.getY();
    }
}
