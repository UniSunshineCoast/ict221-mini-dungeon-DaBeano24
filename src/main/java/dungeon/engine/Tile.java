package dungeon.engine;

import javafx.scene.layout.StackPane;

public abstract class Tile extends StackPane {
    public abstract void interact(Pawn pawn);

    public abstract char getTileSymbol();

}
