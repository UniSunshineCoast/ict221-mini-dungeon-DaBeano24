package dungeon.engine;

public class meleeMutantTile extends Tile {
    private boolean slayed = false;

    @Override
    public void interact(Pawn pawn) {
        pawn.adjustHpAmount(-2);
        pawn.addScore(+2); // Decrease health by 2 and increase score by 2 when interacting with a melee mutant tile
        if (pawn.isGameOver()) {
            System.out.println("You encountered a melee mutant and were defeated :( Game Over.");
        } else {
            System.out.println("You fought a melee mutant and defeated them but lost 2 health. Current health: " + pawn.whatsHp() + ". Your score is now: " + pawn.whatsScore());
        }
    }
    @override
    public char getTileSymbol() {
        return slayed ? '.' : 'M'; // Represents a melee mutant tile, '.' if slayed
    }
    public boolean isSlayed() {
        return slayed;
    }
}
