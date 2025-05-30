package dungeon.engine;

public class GoldTile extends Tile {
    private boolean collected = false;

    @Override
    public void interact(Pawn pawn, int difficulty) {
        if (!collected) {
            pawn.addScore(2);
            collected = true;
            System.out.println("You found gold! Your score is now: " + pawn.whatsScore());
        }
    }

    @Override
    public char getTileSymbol() {
        return collected ? '.' : 'G';
    }

    public boolean isCollected() {
        return collected;
    }
}