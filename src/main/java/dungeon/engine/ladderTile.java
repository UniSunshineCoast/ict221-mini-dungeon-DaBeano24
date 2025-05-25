package dungeon.engine;

public class ladderTile extends Tile {
    private boolean used = false;

    @Override
    public void interact(Pawn pawn) {
        if (!used) {
            used = true;
            System.out.println("You discovered a ladder, I wonder where it leads?");
// *** Reminder to self, don't forget to add the logic for what happens when a ladder is used. Seriously, don't forget, u will fail!!
        }
    }

    @Override
    public char getTileSymbol() {
        return used ? '.' : 'L'; // Represents the Ladder tile
    }
}
