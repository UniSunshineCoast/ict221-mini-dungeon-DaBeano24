package dungeon.engine;

public class TrapTile extends Tile {
    @Override
    public void interact(Pawn pawn, int difficulty) {
        int damage = 2 + Math.min(difficulty, 5); //Damage caps out at 7 from difficulty level 5 onwards.
        pawn.adjustHpAmount( -damage); // Decreases the pawns health by the damage amount
        if (pawn.isGameOver()) {
            System.out.println("You stepped on a trap and lost health! Game Over.");
        } else {
            System.out.println("You stepped on a trap and lost" + damage + " health. Current health: " + pawn.whatsHp());
        }
    }
    @Override
    public char getTileSymbol() {
        return 'T'; // Represents a trap tile
    }
}
