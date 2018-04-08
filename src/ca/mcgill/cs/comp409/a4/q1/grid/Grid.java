package ca.mcgill.cs.comp409.a4.q1.grid;

import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.ObstacleItem;
import ca.mcgill.cs.comp409.a4.q1.grid.items.TileItem;
import ca.mcgill.cs.comp409.a4.q1.grid.util.GridPoint2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static ca.mcgill.cs.comp409.a4.q1.grid.util.CollectionUtils.flattenArray2D;

public class Grid {
    private final TileItem[][] aTileItems;

    public List<CharacterItem> getCharacters() {
        return aCharacters;
    }

    private List<CharacterItem> aCharacters;

    public Grid(int dimX, int dimY) {
        aTileItems = new TileItem[dimY][dimX];
        aCharacters = new LinkedList<>();
    }

    public void initialize(int obstacleCount, int characterCount, int characterSpeedRate) {
        int dimX = aTileItems[0].length;
        int dimY = aTileItems.length;
        LinkedList<GridPoint2D> freeTileIndices = new LinkedList<>();
        GridPoint2D[][] obstacleFreePoints = new GridPoint2D[dimY][dimX];
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                TileItem currentTile = new TileItem(x, y);
                aTileItems[y][x] = currentTile;
                GridPoint2D currentGridPoint = currentTile.getCoordinates();
                freeTileIndices.add(currentGridPoint);
                /* If not on parameter of grid, add point as possible location for obstacles */
                if (x > 0 && x < 29 && y > 0 && y < 29)
                    obstacleFreePoints[y][x] = currentGridPoint;
            }
        }
        LinkedList<GridPoint2D> obstacleFreeIndexList = flattenArray2D(obstacleFreePoints, GridPoint2D.class);
        createObstacles(obstacleCount, obstacleFreeIndexList, freeTileIndices);
        createCharacters(characterCount, characterSpeedRate, freeTileIndices);
    }


    private void createObstacles(int pObstacleCount, LinkedList<GridPoint2D> pObstacleFreeIndices,
                                 LinkedList<GridPoint2D> pFreeTileIndices) {
        /* Copy and shuffle the obstacle free index list so avoid modifying external state */
        LinkedList<GridPoint2D> obstacleFreeIndexList = new LinkedList<>(pObstacleFreeIndices);
        Collections.shuffle(obstacleFreeIndexList);
        for (int i = 0; i < pObstacleCount; i++) {
            GridPoint2D nextObstacleIndex = obstacleFreeIndexList.pop();
            pFreeTileIndices.remove(nextObstacleIndex);
            TileItem obstacleTile = aTileItems[nextObstacleIndex.getY()][nextObstacleIndex.getX()];
            new ObstacleItem(obstacleTile);
        }
    }

    private void createCharacters(int pCharacterCount, int pCharacterSpeedRate, LinkedList<GridPoint2D> pFreeTileIndices) {
        /* Copy list to avoid modifying external state */
        LinkedList<GridPoint2D> freeTileIndexList = new LinkedList<>(pFreeTileIndices);
        /* Shuffle the free tile indices so that characters are placed on random spots */
        Collections.shuffle(freeTileIndexList);
        for (int i = 0; i < pCharacterCount; i++) {
            GridPoint2D nextCharacterIndex = freeTileIndexList.pop();
            pFreeTileIndices.remove(nextCharacterIndex);
            TileItem characterTile = aTileItems[nextCharacterIndex.getY()][nextCharacterIndex.getX()];
            CharacterItem character = new CharacterItem(characterTile, pCharacterSpeedRate);
            aCharacters.add(character);
            updateCharacterTarget(character);
        }
    }

    public void updateCharacterTarget(CharacterItem pCharacterItem) {
        int maxDimX = pCharacterItem.getX() + 8;
        int minDimX = pCharacterItem.getX() - 8;
        int maxDimY = pCharacterItem.getY() + 8;
        int minDimY = pCharacterItem.getY() - 8;
        maxDimX = Math.min(aTileItems[0].length, maxDimX);
        minDimX = Math.max(0, minDimX);
        maxDimY = Math.min(aTileItems.length, maxDimY);
        minDimY = Math.max(0, minDimY);

        List<TileItem> freeNeighborTiles = new ArrayList<>();

        for (int i = minDimY; i < maxDimY; i++) {
            for (int j = minDimX; j < maxDimX; j++) {
                if (!(aTileItems[j][i].getItem() instanceof ObstacleItem))
                    freeNeighborTiles.add(aTileItems[j][i]);
            }
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(0, freeNeighborTiles.size());

        TileItem targetTile = freeNeighborTiles.get(randomIndex);
        pCharacterItem.setTarget(targetTile);
    }

    public TileItem getTile(int x, int y) {
        return aTileItems[y][x];
    }

    /**
     * Visualizes the grid and prints object counts
     */
    public void visualize() {
        int obstacleCount = 0;
        int characterCount = 0;
        int freeTileCount = 0;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                System.out.print('[');
                if (aTileItems[i][j].getItem() instanceof CharacterItem) {
                    System.out.print('C');
                    characterCount++;
                } else if (aTileItems[i][j].getItem() instanceof ObstacleItem) {
                    System.out.print('o');
                    obstacleCount++;
                } else {
                    System.out.print(' ');
                    freeTileCount++;
                }
                System.out.print(']');
            }
            System.out.println();
        }
        System.out.println("Obstacle count: " + obstacleCount);
        System.out.println("Free tile count: " + freeTileCount);
        System.out.println("Character count: " + characterCount);
    }
}
