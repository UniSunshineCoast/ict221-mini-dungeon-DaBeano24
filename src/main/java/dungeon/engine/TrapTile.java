package dungeon.engine;

public class TrapTile extends Tile {
    @Override
    public void interact(Pawn pawn) {
        pawn.adjustHpAmount(-2); // Decrease health by 2 when interacting with a trap
        if (pawn.isGameOver()) {
            System.out.println("You stepped on a trap and lost health! Game Over.");
        } else {
            System.out.println("You stepped on a trap and lost 2 health. Current health: " + pawn.whatsHp());
        }
    }
    @Override
    public char getTileSymbol() {
        return 'T'; // Represents a trap tile
    }
}
