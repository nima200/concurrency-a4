package ca.mcgill.cs.comp409.a4.q1.runnable;

import ca.mcgill.cs.comp409.a4.q1.grid.Grid;
import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.TileItem;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CharacterPlayer implements Callable<Long> {

    private ConcurrentLinkedQueue<CharacterItem> aCharacterItems;
    private Grid aGrid;

    public CharacterPlayer(ConcurrentLinkedQueue<CharacterItem> pCharacterItems, Grid pGrid) {
        assert pCharacterItems != null;
        assert pGrid != null;
        aCharacterItems = pCharacterItems;
        aGrid = pGrid;
    }

    @Override
    public Long call() {
        long start = System.currentTimeMillis();
        long numMoves = 0;
        long twoMinutesInMilli = 2 * 60 * 1000;
        while (start + twoMinutesInMilli > System.currentTimeMillis()) {
            CharacterItem character = aCharacterItems.poll();
            if (character == null) continue;
            try {
                if (tryMoveCharacter(character)) {
                    numMoves++;
                    character.pause();
                    /* Add character back in queue for others to move */
                    aCharacterItems.add(character);
                } else {
                    aGrid.updateCharacterTarget(character);
                    character.pause();
                    aCharacterItems.add(character);
                }
            } catch (InterruptedException pE) {
                System.out.println("Character mover thread unexpectedly interrupted. Exiting");
                System.exit(1);
            }
        }
        return numMoves;
    }

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
