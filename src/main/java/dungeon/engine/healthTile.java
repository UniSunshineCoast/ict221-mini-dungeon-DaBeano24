package dungeon.engine;

public class healthTile extends Tile {
    private boolean used = false;
    @Override
    public void interact(Pawn pawn, int difficulty) {
        pawn.adjustHpAmount(4); // Increase health by 4 when interacting with a health tile
        used = true; // Mark the health tile as used
        pawn.getEngine().log("You found a health tile! Your health is now: " + pawn.whatsHp());
    }

    @Override
    public char getTileSymbol() {
        return used ? '.' : 'H'; // Represents a health tile, '.' if used
    }

    public boolean isUsed() {
        return used;
    }
}
