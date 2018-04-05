package ca.mcgill.cs.comp409.a4.q1;

import ca.mcgill.cs.comp409.a4.q1.grid.Grid;
import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.ObstacleItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.TileItem;
import ca.mcgill.cs.comp409.a4.q1.grid.util.GridPoint2D;

import java.util.*;

public class Driver {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Invalid number of arguments. Expected: 4, Received: " + args.length + ". Exiting...");
            System.exit(1);
        }
        int n = 0, p = 0, r = 0, k = 0;
        try {
            n = Integer.parseInt(args[0]);
            p = Integer.parseInt(args[1]);
            r = Integer.parseInt(args[2]);
            k = Integer.parseInt(args[3]);
        } catch (NumberFormatException pE) {
            System.out.println("Invalid arguments to program. All 4 arguments must be valid numbers. Exiting...");
            System.exit(1);
        }

        Grid grid = new Grid(30, 30);
        grid.initialize(r, n, k);
        grid.visualize();

        TileItem[][] tileGrid = new TileItem[30][30];
        GridPoint2D[][] freeObstacleIndices = new GridPoint2D[30][30];
        GridPoint2D[][] freeTileIndices = new GridPoint2D[30][30];
        for (int i = 0; i < 30; i++) { /* Rows (y) */
            for (int j = 0; j < 30; j++) { /* Columns (x) */
                tileGrid[i][j] = new TileItem(j, i);
                freeTileIndices[i][j] = new GridPoint2D(j, i);
                if (i > 0 && i < 29 && j > 0 && j < 29)
                    /* If not on parameter of grid, add index as possible location for obstacle */
                    freeObstacleIndices[i][j] = new GridPoint2D(j, i);
            }
        }

        /* Create a list of possible indices for obstacles */
        LinkedList<GridPoint2D> freeObstacleIndexList = new LinkedList<>(Arrays.asList(
                 Arrays.stream(freeObstacleIndices).flatMap(Arrays::stream).toArray(GridPoint2D[]::new)));
        /* Remove all null elements representing skipped tiles */
        freeObstacleIndexList.removeAll(Collections.singleton(null));
        Collections.shuffle(freeObstacleIndexList);
        for (int i = 0; i < r; i++) {
            GridPoint2D obstacleIndex = freeObstacleIndexList.pop();
            freeTileIndices[obstacleIndex.getY()][obstacleIndex.getX()] = null;
            TileItem obstacleTile = tileGrid[obstacleIndex.getY()][obstacleIndex.getX()];
            ObstacleItem obstacle = new ObstacleItem(obstacleTile);
            obstacleTile.setItem(obstacle);
        }
        /* Create a list of possible indices that are free without the obstacles */
        LinkedList<GridPoint2D> freeTileIndexQueue = new LinkedList<>(Arrays.asList(
                Arrays.stream(freeTileIndices).flatMap(Arrays::stream).toArray(GridPoint2D[]::new)));
        freeTileIndexQueue.removeAll(Collections.singleton(null));
        /* Create n characters at random locations */
        Collections.shuffle(freeTileIndexQueue);
        List<CharacterItem> characters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            /* Pick and remove the index from the list of free indices */
            GridPoint2D characterIndex = freeTileIndexQueue.pop();
            TileItem characterTile = tileGrid[characterIndex.getY()][characterIndex.getX()];
            CharacterItem character = new CharacterItem(characterTile, k);
            characterTile.setItem(character);
            characters.add(character);
            /* Pick a remove random target vertex for character */
            GridPoint2D targetIndex = freeTileIndexQueue.pop();
            TileItem targetTile = tileGrid[targetIndex.getY()][targetIndex.getX()];
            character.setTarget(targetTile);
        }

        CharacterItem character = characters.get(0);
        System.out.println("Character current coords: " + character.getCurrentTile().getCoordinates().getX() + ", " + character.getCurrentTile().getCoordinates().getY());
        System.out.println("Character target coords: " + character.getTargetTile().getCoordinates().getX() + ", " + character.getTargetTile().getCoordinates().getY());
        while (!character.atTargetLocation()) {
            TileItem targetTile = character.getTargetTile();
            GridPoint2D targetCoords = targetTile.getCoordinates();
            GridPoint2D currentCoords = character.getCurrentTile().getCoordinates();
            int dx = 0;
            int dy = 0;
            int x;
            int y;
            if (targetCoords.getX() > currentCoords.getX()) {
                dx++;
            } else if (targetCoords.getX() < currentCoords.getX()) {
                dx--;
            }
            if (targetCoords.getY() > currentCoords.getY()) {
                dy++;
            } else if (targetCoords.getY() < currentCoords.getY()) {
                dy--;
            }
            x = currentCoords.getX() + dx;
            y = currentCoords.getY() + dy;
            TileItem nextTile = tileGrid[y][x];
            if (!character.tryMove(nextTile)) {
                System.out.println("Reached obstacle");
                break;
            } else {
                System.out.println("Moved to " + x + ", " + y);
            }
        }
        if (character.atTargetLocation()) {
             System.out.println("Character has reached destination");
        }

//        visualizeGrid(tileGrid);
    }
}
