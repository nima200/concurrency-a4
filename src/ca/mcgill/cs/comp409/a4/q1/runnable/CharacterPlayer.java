package ca.mcgill.cs.comp409.a4.q1.runnable;

import ca.mcgill.cs.comp409.a4.q1.grid.Grid;
import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.TileItem;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CharacterPlayer implements Runnable {

    private ConcurrentLinkedQueue<CharacterItem> aCharacterItems;
    private Grid aGrid;

    public CharacterPlayer(ConcurrentLinkedQueue<CharacterItem> pCharacterItems, Grid pGrid) {
        assert pCharacterItems != null;
        assert pGrid != null;
        aCharacterItems = pCharacterItems;
        aGrid = pGrid;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long twoMinutesInMilli = 2 * 60 * 1000;
        while (start + twoMinutesInMilli > System.currentTimeMillis()) {
            CharacterItem character = aCharacterItems.poll();
            if (character == null) continue;
            /* If character is not ready to be moves, skip and add back to queue */
            if (character.isReady()) {
                /* If character was not able to move a spot, update it to have a new target */
                if (!tryMoveCharacter(character)) {
                    aGrid.updateCharacterTarget(character);
                }
                /* Update the character's pause time */
                character.pause();
            }
            /* Add character back in queue for others to move */
            aCharacterItems.add(character);
        }
    }

    /**
     * Attempts to move the character to it's next position in the path to it's target.
     * @param pCharacter Character to try to move
     * @return True of character made movement progress towards target, false if character
     * failed by hitting an obstacle or attempting to move to an occupied tile.
     */
    private boolean tryMoveCharacter(CharacterItem pCharacter) {
        assert pCharacter != null;
        if (pCharacter.atTargetLocation()) {
            return false;
        }
        TileItem targetTile = pCharacter.getTargetTile();
        TileItem currentTile = pCharacter.getCurrentTile();
        int dx = 0, dy = 0, x, y;
        if (targetTile.getX() > currentTile.getX()) dx++;
        else if (targetTile.getX() < currentTile.getX()) dx--;
        if (targetTile.getY() > currentTile.getY()) dy++;
        else if (targetTile.getY() < currentTile.getY()) dy--;

        x = currentTile.getX() + dx;
        y = currentTile.getY() + dy;
        TileItem nextTile = aGrid.getTile(x, y);

        return pCharacter.tryMove(nextTile);
    }
}
