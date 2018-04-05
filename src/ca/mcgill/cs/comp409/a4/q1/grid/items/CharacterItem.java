package ca.mcgill.cs.comp409.a4.q1.grid.items;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class CharacterItem implements GridItem {

    private TileItem aCurrentTile;
    private TileItem aTargetTile;
    private int aMoveRate;
    private int aRandomSeed;

    public CharacterItem(TileItem pCurrentTile, int pSpeed) {
        aMoveRate = pSpeed;
        aCurrentTile = pCurrentTile;
        aTargetTile = new TileItem(-1, -1);
        pCurrentTile.setItem(this);
        aRandomSeed = ThreadLocalRandom.current().nextInt(1, 5);
    }

    public void setTarget(TileItem pTargetTile) {
        aTargetTile = pTargetTile;
    }

    /**
     * Attempts to move the character to a given tile. Character will first attempt to gain control over the locks of
     * its current tile and the tile it is trying to move to.
     * @implNote Must be a tile within the 1 unit radius of character's current tile
     * @param pTile Tile to attempt to move character to
     * @return True if tile was free and character was able to move to it, false otherwise.
     */
    public boolean tryMove(TileItem pTile) {
        /* Characters can only move 1 unit at a time */
        assert aTargetTile != null;
        assert Math.abs(aCurrentTile.getCoordinates().getX() - pTile.getCoordinates().getX()) <= 1;
        assert Math.abs(aCurrentTile.getCoordinates().getY() - pTile.getCoordinates().getY()) <= 1;
        /* Gain control over current tile and tile moving to */
        acquireTiles(aCurrentTile, pTile);
        if (!pTile.isFree()) {
            releaseTiles(aCurrentTile, pTile);
            return false;
        }
        /* Assign the current tile as free, update current tile and make sure new current tile has character as occupant */
        aCurrentTile.removeItem();
        pTile.setItem(this);
        /* Release control over tiles for others to proceed */
        releaseTiles(aCurrentTile, pTile);
        aCurrentTile = pTile;
        return true;
    }

    public int getX() {
        return aCurrentTile.getX();
    }

    public int getY() {
        return aCurrentTile.getY();
    }

    private void acquireTiles(TileItem... pTiles) {
        Arrays.sort(pTiles);
        for (TileItem tile :
                pTiles) {
            tile.acquire();
        }
    }

    private void releaseTiles(TileItem... pTiles) {
        Arrays.sort(pTiles);
        for (TileItem tile :
                pTiles) {
            tile.release();
        }
    }

    public boolean atTargetLocation() {
        return aCurrentTile.equals(aTargetTile);
    }

    public void pause() throws InterruptedException {
        Thread.sleep(aMoveRate * aRandomSeed);
    }

    @Override
    public TileItem getCurrentTile() {
        return aCurrentTile;
    }

    public TileItem getTargetTile() {
        return aTargetTile;
    }
}
