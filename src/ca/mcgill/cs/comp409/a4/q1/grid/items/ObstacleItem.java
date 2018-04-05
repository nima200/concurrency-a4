package ca.mcgill.cs.comp409.a4.q1.grid.items;

public class ObstacleItem implements GridItem {

    private TileItem aCurrentTile;

    public ObstacleItem(TileItem pCurrentTile) {
        aCurrentTile = pCurrentTile;
        pCurrentTile.setItem(this);
    }

    @Override
    public TileItem getCurrentTile() {
        return aCurrentTile;
    }
}
