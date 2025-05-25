package dungeon.engine;

import javafx.scene.text.Text;

public class GameEngine {

    /**
     * An example board to store the current game state.
     *
     * Note: depending on your game, you might want to change this from 'int' to String or something?
     */
    private Tile[][] map;
    private Pawn pawn;

    /**
     * Creates a square game board.
     *
     * @param size the width and height.
     */
    public GameEngine(int size) {
        map = new Tile[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Tile tile = new defaultTile();
                Text text = new Text(i + "," + j);
                tile.getChildren().add(text);
                map[i][j] = new defaultTile();
            }
        }
        // Adding entry tile where the pawn spawns in.
        for (int i = 0; i < 5; i++) placeRandomTile(new TrapTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new healthTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new rangedMutantTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new meleeMutantTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new GoldTile());
        placeRandomTile(new ladderTile());

        System.out.println("Map Generated")


        map[0][0].setStyle("-fx-background-color: #7baaa4");
        map[size-1][size-1].setStyle("-fx-background-color: #7baaa4");
    }

    private void placeRandomTile(Tile tile) {
        int row, column;
        do {
            row = (int) (Math.random() * getSize());
            column = (int) (Math.random() * getSize());
        } while (!(map[row][column] instanceof defaultTile));

        map[row][column] = tile; // Place the tile at the random position
    }

    // Pawn Movement Section
    public void movePawn(String direction) {
        int newRow = pawn.whatsRow();
        int newColumn = pawn.whatsColumn();

        switch (direction.toLowerCase()) {
            case "u": newRow--; break; // Move up
            case "d": newRow++; break; // Move Down
            case "r": newColumn++; break; // Move Right
            case "l": newColumn--; break; // Move Left
            default:
                System.out.println("Invalid movement, Instead use U D R L Keys");
                return;

        }

        // Check for Borders
        if (newRow) < 0 || newRow >= getSize() || newColumn < 0 || newColumn >= getSize()) {
            System.out.println("You smashed your head against a wall, try again");
            return;

        }

        // Tile Interaction
        Tile tile = map[newRow][newColumn];
        tile.interact(pawn);

        // Update Pawn Position
        pawn.setLocation(newRow, newColumn);

        //Ranged Mutant Check
        checkRangedMutantAttack(); // Checks to see if the pawn is attacked after moving.
        
    }

    private void checkRangedMutantAttack() {
        int row = pawn.whatsRow();
        int column = pawn.whatsColumn();

        int[][] directions = { {-2,0}, {2,0}, {0,-2}, {0,2} };
        for (int[] dir : directions) {
            int checkRow = row + dir[0];
            int checkColumn = column + dir[1];

            if (checkRow >= 0 && checkRow < getSize() && checkColumn >= 0 && checkColumn < getSize()) {
                Tile tile = map[checkRow][checkColumn];
                if (tile instanceof rangedMutantTile ranged && !ranged.isSlayed()) {
                    if (Math.random() < 0.5) { // Adds the 50% chance of being attacked by the ranged mutant when in range.
                        pawn.adjustHpAmount(-2); // Decrease health by 2 when attacked by the mutant
                        System.out.println("You were attacked by a ranged Mutant, Ouch! Your health is now: " + pawn.whatsHp());
                    } else {
                        System.out.println("A ranged mutant shot at you but missed, slay it before it can attack again.")
                        }
                    // Checks to see if the attack has killed the pawn.
                    if (pawn.isGameOver()) {
                        System.out.println("Your Heart was Pierced by a ranged mutants arrow (you kinda need that organ) Game Over.");
                    }
                }
            }

        })
    }

    /**
     * The size of the current game.
     *
     * @return this is both the width and the height.
     */
    public int getSize() {
        return map.length;
    }

    /**
     * The map of the current game.
     *
     * @return the map, which is a 2d array.
     */
    public Tile[][] getMap() {
        return map;
    }

    /**
     * Plays a text-based game
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(10);

        engine.pawn = new Pawn(9, 0); // Sets the start location of the pawn at the bottem left hand corner of the map.

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (!engine.pawn.isGameOver()) {
            System.out.print("Move (U/D/R/L): ");
            String input = scanner.nextLine();

            engine.movePawn(input);

            System.out.println("HP: " + engine.pawn.whatsHp());
            System.out.println("Steps: " + engine.pawn.whatsSteps());

        }
        System.out.println("Game Over! Your score is: " + engine.pawn.whatsScore());

        }

        System.out.printf("The size of map is %d * %d\n", engine.getSize(), engine.getSize()); // ******Original code dont remove just yet*****
    }
}
