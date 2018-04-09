package ca.mcgill.cs.comp409.a4.q1.grid.items;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class CharacterItem implements GridItem {

    private TileItem aCurrentTile;
    private TileItem aTargetTile;
    private int aMoveRate;
    private int aRandomSeed;
    private int aMoveCount = 0;
    private long lastPauseTime;

    public CharacterItem(TileItem pCurrentTile, int pSpeed) {
        aMoveRate = pSpeed;
        aCurrentTile = pCurrentTile;
        aTargetTile = new TileItem(-1, -1);
        pCurrentTile.setItem(this);
        aRandomSeed = ThreadLocalRandom.current().nextInt(1, 5);
    }

    /**
     * Setter method for updating the character's target tile
     * @param pTargetTile New tile to set as character's target tile
     */
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
        /* Gain control over current tile and tile moving to */
        acquireTiles(aCurrentTile, pTile);
        if (!pTile.isFree()) {
            releaseTiles(aCurrentTile, pTile);
            return false;
        }
        /* Assign the current tile as free, update current tile and make sure new current tile has character as occupant */
        aCurrentTile.removeItem();
        pTile.setItem(this);
        aMoveCount++;
        /* Release control over tiles for others to proceed */
        releaseTiles(aCurrentTile, pTile);
        aCurrentTile = pTile;
        return true;
    }

    /**
     * Getter for the x coordinate of the character item's current tile
     * @return X coordinate for character item's current tile
     */
    public int getX() {
        return aCurrentTile.getX();
    }

    /**
     * Getter for the y coordinate of the character item's current tile
     * @return Y coordinate for character item's current tile
     */
    public int getY() {
        return aCurrentTile.getY();
    }

    /**
     * Locks the set of tiles provided in order
     * @param pTiles Implicit array of tiles
     */
    private void acquireTiles(TileItem... pTiles) {
        Arrays.sort(pTiles);
        for (TileItem tile :
                pTiles) {
            tile.acquire();
        }
    }

    /**
     * Releases the set of tiles provided in order
     * @param pTiles Implicit array of tiles
     */
    private void releaseTiles(TileItem... pTiles) {
        for (TileItem tile :
                pTiles) {
            tile.release();
        }
    }

    /**
     * Checks to see if character's current tile is the same as its target tile
     * @return True if character is at it's target tile, false otherwise
     */
    public boolean atTargetLocation() {
        return aCurrentTile.equals(aTargetTile);
    }

    /**
     * Updates the 'last pause time' of the character item to be current time
     */
    public void pause() {
        lastPauseTime = System.currentTimeMillis();
    }

    /**
     * Checks to see if the character has waited long enough, given its 'last pause time'
     * @return True if character item has waited enough, false otherwise
     */
    public boolean isReady() {
        return System.currentTimeMillis() >= lastPauseTime + (aMoveRate * aRandomSeed);
    }

    @Override
    public TileItem getCurrentTile() {
        return aCurrentTile;
    }

    /**
     * Getter method for target tile of the character item
     * @return Target tile
     */
    public TileItem getTargetTile() {
        return aTargetTile;
    }

    /**
     * Getter method for the character item current move count
     * @return Number of successful moves made by the character item so far
     */
    public int getMoveCount() {
        return aMoveCount;
    }
}
