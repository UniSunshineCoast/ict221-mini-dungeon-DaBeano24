package dungeon.engine;

import javafx.scene.text.Text;
import java.util.Scanner;

public class GameEngine {

    private Tile[][] map;
    private Pawn pawn;

    // Step Settings
    private int maxSteps = 100;
    private int stepsTaken = 0;

    private void updateMaxSteps() {
        if (difficulty >= 6) {
            maxSteps = 100 - (difficulty - 5) * 10; // Decrease max steps based on difficulty
        } else {
            maxSteps = 100; // Default max steps
        }
    }

    public void levelAdvance() {
        level++;

        if (level > 2) {
            System.out.println("You have completed the game! Congratulations!");
            System.exit(0); // Ends the game after level 2
        }

    map = new Tile[getSize()][getSize()];
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                map[i][j] = new defaultTile();
            }
        }

        // Re-populate the map with tiles based on the new level
        for (int i = 0; i < 5 + level; i++) placeRandomTile(new TrapTile());
        for (int i = 0; i < 2 + level; i++) placeRandomTile(new healthTile());
        for (int i = 0; i < 3 + level; i++) placeRandomTile(new rangedMutantTile());
        for (int i = 0; i < 3 + level; i++) placeRandomTile(new meleeMutantTile());
        for (int i = 0; i < 2 + level; i++) placeRandomTile(new GoldTile());
        placeRandomTile(new ladderTile());

        // Spawn the pawn at the bottom left corner of the new map
        pawn.setLocation(getSize() - 1, 0);

        System.out.println("Level " + level + " generated.");
    }

    // section to track the active level of the game
    private int level = 1;

    public int getLevel() {
        return level;
    }

    // Game Difficulty Section
    private int difficulty = 3;
    public GameEngine(int size, int difficulty) {
        this.difficulty = Math.max(0, Math.min(10, difficulty)); // Ensure difficulty is between 0 and 10
        this.level = 1;
        updateMaxSteps();

        this.map = new Tile[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = new defaultTile(); // Initialize the map with default tiles
            }
        }

        for (int i = 0; i < 5; i++) placeRandomTile(new TrapTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new healthTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new rangedMutantTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new meleeMutantTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new GoldTile());

        placeRandomTile(new ladderTile());
        System.out.println("Map generated with size: " + size + " and difficulty: " + difficulty);
        map[0][0].setStyle("-fx-background-color: #7baaa4");
        map[size - 1][size - 1].setStyle("-fx-background-color: #7baaa4");
    }
    public int getSize() {
        return map.length;
    }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int d ) {
        this.difficulty = Math.max(0, Math.min(10, d));
        updateMaxSteps(); // Update max steps based on new difficulty
    }
    public void increaseDifficulty(int amount) {
        this.difficulty = Math.min(10, this.difficulty + amount);
        updateMaxSteps(); // recalculate max steps
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
        if (newRow < 0 || newRow >= getSize() || newColumn < 0 || newColumn >= getSize()) {
            System.out.println("You smashed your head against a wall, try again");
            return;

        }

        // Tile Interaction
        Tile tile = map[newRow][newColumn];
        tile.interact(pawn, difficulty);

        // Reverts slayed Mutant Tiles to defaultTiles
        if ((tile instanceof meleeMutantTile m && m.isSlayed()) ||
                (tile instanceof rangedMutantTile r && r.isSlayed())) {
            map[newRow][newColumn] = new defaultTile();
        }

        // Update Pawn Position
        pawn.setLocation(newRow, newColumn);
        stepsTaken++;
        if (stepsTaken >= maxSteps) {
            System.out.println("You have run out of steps, You collapse from exhaustion and die. Game Over.");
            pawn.adjustHpAmount(-pawn.whatsHp()); // Set health to 0
            return;
        }

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
                    if (Math.random() < 0.5) {
                        int damage = 2 + (int)(difficulty * 0.2); // Adds the 50% chance of being attacked by the ranged mutant when in range.
                        pawn.adjustHpAmount(-damage); // Decrease health by 2 when attacked by the mutant
                        System.out.println("You were attacked by a ranged Mutant, Ouch! Your health is now: " + pawn.whatsHp());
                    } else {
                        System.out.println("A ranged mutant shot at you but missed, slay it before it can attack again.");
                        }
                    // Checks to see if the attack has killed the pawn.
                    if (pawn.isGameOver()) {
                        System.out.println("Your Heart was Pierced by a ranged mutants arrow (you kinda need that organ) Game Over.");
                    }
                }
            }

        }
    }

    /**
     * The size of the current game.
     *
     * @return this is both the width and the height.
     */

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

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select the games difficulty, be warned it will directly impact the damage you take from hostiles");
        String input = scanner.nextLine();
        int difficulty = 3; // Default difficulty
        try {
            difficulty = Integer.parseInt(input);
            if (difficulty < 0 || difficulty > 10) difficulty = 3;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, setting difficulty to default (3).");
        }
        GameEngine engine = new GameEngine(10, difficulty);

        engine.pawn = new Pawn(9, 0); // Sets the start location of the pawn at the bottem left hand corner of the map.
        engine.pawn.setEngine(engine);


        while (!engine.pawn.isGameOver()) {
            System.out.print("Move (U/D/R/L): ");
            input = scanner.nextLine().trim();
            engine.movePawn(input);
            System.out.println("HP: " + engine.pawn.whatsHp());
            System.out.println("Steps: " + engine.stepsTaken + "/" + engine.maxSteps);

        }
        System.out.println("Game Over! Your score is: " + engine.pawn.whatsScore());
        System.out.printf("The size of map is %d * %d\n", engine.getSize(), engine.getSize()); // ******Original code dont remove just yet*****

        }


}
