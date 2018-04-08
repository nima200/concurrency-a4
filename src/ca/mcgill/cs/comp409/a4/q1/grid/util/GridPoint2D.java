package ca.mcgill.cs.comp409.a4.q1.grid.util;

import java.util.Objects;

public class GridPoint2D {

    private int x;
    private int y;

    public GridPoint2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object pO) {
        if (this == pO) return true;
        if (pO == null || getClass() != pO.getClass()) return false;
        GridPoint2D that = (GridPoint2D) pO;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
