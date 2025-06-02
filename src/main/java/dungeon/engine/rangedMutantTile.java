package dungeon.engine;

public class rangedMutantTile extends Tile{
    private boolean slayed = false;

    @Override
    public void interact(Pawn pawn, int difficulty) {
        if (!slayed) {
            pawn.addScore(2);
            slayed = true;
            pawn.getEngine().log("You encountered a ranged mutant and have slain them Hurrah! Your score is now: " + pawn.whatsScore());
        }
    }
    @Override
    public char getTileSymbol() {
        return slayed ? '.' : 'R'; // Represents a ranged mutant tile, '.' if slayed
    }

    public boolean isSlayed() {
        return slayed;
    }
}
