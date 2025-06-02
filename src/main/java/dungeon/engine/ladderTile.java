package dungeon.engine;

public class ladderTile extends Tile {
    private boolean used = false;

    @Override
    public void interact(Pawn pawn, int difficulty) {
        if (!used) {
            used = true;
            GameEngine engine = pawn.getEngine();
            engine.log("You discovered a ladder, I wonder where it leads?");
            if (engine.getLevel() == 1) {
                engine.increaseDifficulty(2);
                engine.levelAdvance();
            }
        }
    }

    @Override
    public char getTileSymbol() {
        return used ? '.' : 'L';
    }
}
