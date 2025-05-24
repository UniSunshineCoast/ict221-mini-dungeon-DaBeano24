package dungeon.engine;

public class GoldTile extends Tile {
    private boolean collected = false;

    @override
    public void interact (Pawn pawn) {
        pawn.addScore(2); // Increase score by 2 when interacting with a gold tile
        System.out.println("You found gold! Your score is now: " + pawn.whatsScore());
    }

    @override
    public char getTileSymbol() {
        return 'G'; // Represents a gold tile
    }

    public char getTileSymbol(Pawn pawn) {
        return collected ? '.' : 'G'; // Represents a gold tile, '.' if collected
    }
    public boolean isCollected() {
        return collected;
    }
}
