package dungeon.engine;

public class rangedMutantTile extends Tile{
    private boolean slayed = false;

    @override
    void interact(Pawn pawn) {
        if (!slayed) {
            pawn.addScore(2);
            slayed = true;
            System.out.println("You encountered a ranged mutant and have slain them Hurrah! Your score is now: " + pawn.whatsScore());
        }
    }
    @override
    public char getTileSymbol() {
        return slayed ? '.' : 'R'; // Represents a ranged mutant tile, '.' if slayed
    }

    public boolean isSlayed() {
        return slayed;
    }
}
