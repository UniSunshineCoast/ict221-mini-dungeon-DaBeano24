package dungeon.engine;

public class ladderTile extends Tile {
    private boolean used = false;

    @Override
    public void interact(Pawn pawn, int difficulty) {
        if (!used) {
            used = true;
            System.out.println("You discovered a ladder, I wonder where it leads?");
            pawn.getEngine().increaseDifficulty(2); // Increase the difficulty of the game by 2 when interacting with a ladder tile
            pawn.getEngine().levelAdvance(); // Advance to the next level
        }
    }

    @Override
    public char getTileSymbol() {
        return used ? '.' : 'L'; // Represents the Ladder tile
    }
}
