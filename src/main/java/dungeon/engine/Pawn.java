package dungeon.engine;

public class Pawn {
    private int row;
    private int column;
    private int health = 10;
    private int score = 0;
    private int steps = 0;
    private final int maxHealth = 10;
    private boolean gameOver = false;
    private boolean gameWon = false;

    // Leveling and difficulty system
    private GameEngine engine;
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }
    public GameEngine getEngine() {
        return engine;
    }

    public Pawn(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // Pawn Movement
    public void moveRight() { column++; steps++; }
    public void moveLeft() { column--; steps++; }
    public void moveUp() { row--; steps++; }
    public void moveDown() { row++; steps++; }

    // Health System
    public void adjustHpAmount(int hpChange) {
        health += hpChange;
        if (health <= 0) {
            health = 0;
            gameOver = true;
        }
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameWon(boolean won) {
        gameWon = won;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int whatsHp() { return health; }

    // Score tracking system
    public void addScore(int sAmount) {
        score += sAmount;
        if (score < 0) score = 0;
    }
    public int whatsScore() { return score; }

    // retrieves the number of steps taken
    public int whatsSteps() { return steps; }

    // location management system
    public int whatsRow() { return row; }
    public int whatsColumn() { return column; }
    public void setLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }
}