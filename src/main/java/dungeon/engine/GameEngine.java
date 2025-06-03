package dungeon.engine;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static java.lang.System.out;
import java.time.LocalDate;


public class GameEngine {

    private Tile[][] map;
    private Pawn pawn;

    public Pawn getPawn() {
        return pawn;
    }

    private java.util.function.Consumer<String> logger = null;
    public void setLogger(java.util.function.Consumer<String> logger) {
        this.logger = logger;
    }

    public void log(String message) {
        if (logger != null) {
            logger.accept(message);
        } else {
            out.println(message); // Console output as fallback
        }
    }

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

        map = new Tile[getSize()][getSize()];
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                map[i][j] = new defaultTile();
            }
        }

        for (int i = 0; i < 5 + level; i++) placeRandomTile(new TrapTile());
        for (int i = 0; i < 2 + level; i++) placeRandomTile(new healthTile());
        for (int i = 0; i < 3 + level; i++) placeRandomTile(new rangedMutantTile());
        for (int i = 0; i < 3 + level; i++) placeRandomTile(new meleeMutantTile());
        for (int i = 0; i < 2 + level; i++) placeRandomTile(new GoldTile());
        placeRandomTile(new ladderTile());

        pawn.setLocation(getSize() - 1, 0);
        log("Level " + level + " generated.");
        stepsTaken = 0;
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
        // Place initial tiles on the map
        for (int i = 0; i < 5; i++) placeRandomTile(new TrapTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new healthTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new rangedMutantTile());
        for (int i = 0; i < 3; i++) placeRandomTile(new meleeMutantTile());
        for (int i = 0; i < 2; i++) placeRandomTile(new GoldTile());

        placeRandomTile(new ladderTile());

        // Set the pawn's initial position
        this.pawn = new Pawn(size - 1, 0); // Start at the bottom left corner
        this.pawn.setEngine(this); // Set the engine for the pawn

        log("Map generated with size: " + size + " and difficulty: " + difficulty);
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
        if (pawn.isGameOver()) {
            log("Game Over! You cannot move anymore.");
            return;

        }

        int newRow = pawn.whatsRow();
        int newColumn = pawn.whatsColumn();

        switch (direction.toLowerCase()) {
            case "u": newRow--; break; // Move up
            case "d": newRow++; break; // Move Down
            case "r": newColumn++; break; // Move Right
            case "l": newColumn--; break; // Move Left
            default:
                log("Invalid movement, Instead use U D R L Keys");
                return;

        }

        // Check for Borders
        if (newRow < 0 || newRow >= getSize() || newColumn < 0 || newColumn >= getSize()) {
            log("You smashed your head against a wall, try again");
            return;

        }
// Tile Interaction
        Tile tile = map[newRow][newColumn];
        int currentLevel = level;
        tile.interact(pawn, difficulty);

// If level has changed due to ladder interaction, stop processing
        if (level > currentLevel) {
            return;
        }

// Win condition: Ladder on Level 2
        if (tile instanceof ladderTile && level == 2 && !pawn.isGameOver()) {
            log("You climbed up the ladder and escaped the dungeon! You win!");
            pawn.setGameWon(true); // << You will add this method
            pawn.adjustHpAmount(-pawn.whatsHp()); // Ends the game
            return;
        }




        // Replace GoldTiles with defaultTiles after interaction
        if (tile instanceof GoldTile gold && gold.isCollected()) {
            map[newRow][newColumn] = new defaultTile();
        }

        // Reverts healthTiles to defaultTiles after interaction
        if (tile instanceof healthTile h && h.isUsed()) {
            map[newRow][newColumn] = new defaultTile();
        }

        // Reverts slayed Mutant Tiles to defaultTiles
        if ((tile instanceof meleeMutantTile m && m.isSlayed()) ||
                (tile instanceof rangedMutantTile r && r.isSlayed())) {
            map[newRow][newColumn] = new defaultTile();
        }

        // Update Pawn Position
        pawn.setLocation(newRow, newColumn);
        stepsTaken++;
        if (stepsTaken >= maxSteps) {
            log("You have run out of steps, You collapse from exhaustion and die. Game Over.");
            pawn.adjustHpAmount(-pawn.whatsHp()); // Set health to 0
            return;
        }

        //Ranged Mutant Check
        checkRangedMutantAttack(); // Checks to see if the pawn is attacked after moving.

        if (pawn.isGameOver()) {
            log("You met a tragic end, how unfortunate, Game Over.");
            return;
        }
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
                        log("You were attacked by a ranged Mutant, Ouch! Your health is now: " + pawn.whatsHp());
                    } else {
                        log("A ranged mutant shot at you but missed, slay it before it can attack again.");
                    }
                    // Checks to see if the attack has killed the pawn.
                    if (pawn.isGameOver()) {
                        log("Your Heart was Pierced by a ranged mutants arrow (you kinda need that organ) Game Over.");
                        return;
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

    public void
    saveGame(String filename) {
        log("Entered saveGame method");
        try (java.io.PrintWriter out = new java.io.PrintWriter(filename)) {
            // Save the game stats
            out.println(level);
            out.println(difficulty);
            out.println(stepsTaken);
            out.println(pawn.whatsHp());
            out.println(pawn.whatsScore());
            out.println(pawn.whatsColumn());
            out.println(pawn.whatsRow());

            // Save the Map
            for (int i = 0; i < getSize(); i++) {
                for (int j = 0; j < getSize(); j++) {
                    out.print(map[i][j].getTileSymbol());
                }
                out.println();
            }
            log("Game saved successfully.");
        } catch (java.io.IOException e) {
            System.out.println("IOException caught: " + e.getMessage());
            e.printStackTrace();
            log("Error saving game: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
            log("An unexpected error occurred while saving the game: " + e.getMessage());
        }
    }

    // Load the game from a file
    public void loadGame(String filename) {
        try (java.util.Scanner in = new java.util.Scanner(new java.io.File(filename))) {
            // Load the game stats
            level = Integer.parseInt(in.nextLine());
            difficulty = Integer.parseInt(in.nextLine());
            stepsTaken = Integer.parseInt(in.nextLine());
            int hp = Integer.parseInt(in.nextLine());
            int score = Integer.parseInt(in.nextLine());
            int column = Integer.parseInt(in.nextLine());
            int row = Integer.parseInt(in.nextLine());

            updateMaxSteps(); // Update max steps based on loaded difficulty

            // Initialize the map
            map = new Tile[getSize()][getSize()];
            for (int i =0; i < getSize(); i++) {
                String line = in.nextLine();
                for (int j = 0; j < getSize(); j++) {
                    char symbol = line.charAt(j);
                    map[i][j] = createTileFromSymbol(symbol);
                }
            }
            // Set the pawn's state
            pawn = new Pawn(row, column);
            pawn.setEngine(this);
            pawn.adjustHpAmount(hp - pawn.whatsHp()); // Adjust health to match loaded value
            pawn.addScore(score - pawn.whatsScore()); // Adjust score to match loaded value

            log("Game loaded successfully from " + filename);
        } catch (Exception e) {
            log("Error loading game: " + e.getMessage());
        }
    }
    private final String topScoresFile = "topScores.dat";
    private List<TopScore> topScores = new ArrayList<>();

    public void loadTopScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new java.io.FileInputStream(topScoresFile))) {
            topScores =(List<TopScore>)ois.readObject();
        } catch (Exception e) {
            topScores = new ArrayList<>(); // Initialize an empty list if loading fails
        }
    }

    public void saveTopScores() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(topScoresFile))) {
            oos.writeObject(topScores);
        } catch (java.io.IOException e) {
            log("Error saving top scores: " + e.getMessage());
        }
    }

    public boolean updateTopScores(int finalScore) {
        TopScore newScore = new TopScore(finalScore, LocalDate.now());
        topScores.add(newScore);
        topScores.sort(null);
        if (topScores.size() > 5) {
            topScores = topScores.subList(0, 5);
        }
        saveTopScores();
        return topScores.contains(newScore);
    }
    public List<TopScore> getTopScores() {
        return new ArrayList<>(topScores); // Return a copy of the top scores list
    }

    // Create a Tile based on the symbol
    private Tile createTileFromSymbol(char symbol) {
        return switch (symbol) {
            case 'T' -> new TrapTile();
            case 'H' -> new healthTile();
            case 'R' -> new rangedMutantTile();
            case 'M' -> new meleeMutantTile();
            case 'G' -> new GoldTile();
            case 'L' -> new ladderTile();
            default -> new defaultTile(); // Default tile for any other symbol
        };
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Plays a text-based game
     */

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        out.println("Please select the games difficulty, be warned it will directly impact the damage you take from hostiles");
        String input = scanner.nextLine();
        int difficulty = 3; // Default difficulty
        try {
            difficulty = Integer.parseInt(input);
            if (difficulty < 0 || difficulty > 10) difficulty = 3;
        } catch (NumberFormatException e) {
            out.println("Invalid input, setting difficulty to default (3).");
        }
        GameEngine engine = new GameEngine(10, difficulty);
        engine.setLogger(out::println); // Set the logger to print to console

        engine.pawn = new Pawn(9, 0); // Sets the start location of the pawn at the bottem left hand corner of the map.
        engine.pawn.setEngine(engine);


        while (!engine.pawn.isGameOver()) {
            engine.log("Move (U/D/R/L): ");
            input = scanner.nextLine().trim();
            engine.movePawn(input);
            engine.log("HP: " + engine.pawn.whatsHp());
            engine.log("Steps: " + engine.stepsTaken + "/" + engine.maxSteps);

        }
        engine.log("Game Over! Your score is: " + engine.pawn.whatsScore());
        out.printf("The size of map is %d * %d\n", engine.getSize(), engine.getSize()); // ******Original code dont remove just yet*****

    }


}