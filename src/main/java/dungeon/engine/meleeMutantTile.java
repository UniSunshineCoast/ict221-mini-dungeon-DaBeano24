package dungeon.engine;

public class meleeMutantTile extends Tile {
    private boolean slayed = false;

    @Override
    public void interact(Pawn pawn, int difficulty) {
        int damage = 2 + Math.min(difficulty, 5); // Damage increases with difficulty, max 5
        pawn.adjustHpAmount(-damage);
        pawn.addScore(+2); // Subtract damage taken and increase score by 2 when interacting with a melee mutant tile
        slayed = true; // Mark the melee mutant as slayed
        // Check if the pawn is defeated
        if (pawn.isGameOver()) {
            pawn.getEngine().log("You encountered a melee mutant and were defeated :( Game Over.");
        } else {
            pawn.getEngine().log("You fought a melee mutant and defeated them but lost 2 health. Current health: " + pawn.whatsHp() + ". Your score is now: " + pawn.whatsScore());
        }
    }
    @Override
    public char getTileSymbol() {
        return slayed ? '.' : 'M'; // Represents a melee mutant tile, '.' if slayed
    }
    public boolean isSlayed() {
        return slayed;
    }
}
