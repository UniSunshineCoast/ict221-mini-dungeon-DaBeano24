package dungeon.engine;

public class defaultTile extends Tile {
    @Override
    public void interact(Pawn pawn) {
        // Default interaction does nothing
    }

    @Override
    public char getTileSymbol() {
            return '.'; // Represents an empty tile
        }
}
