package dungeon.gui;

import dungeon.engine.GoldTile;
import dungeon.engine.Tile;
import dungeon.engine.GameEngine;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;

public class Controller {

    private void logMessage(String message) {
        gameLog.appendText(message + "\n");
    }

    private boolean gameOverHandled = false;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label hpLabel;

    @FXML
    private Label stepsLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private TextArea gameLog;

    @FXML
    private GameEngine engine;

    @FXML
    public void initialize() {
        engine = new GameEngine(10, 3); // Initialise the initial game with a size ten map and a difficulty level of 3.
        engine.loadTopScores();
        engine.setLogger(this::logMessage);
        updateGui();
        displayTopScores();
        logMessage("Welcome to the Dungeon Game! Use the buttons to move your pawn.");
    }

    @FXML
    public void onUpButtonPressed() {
        engine.movePawn("u");
        updateGui();
        logMessage("Moved Up");
    }

    @FXML
    public void onDownButtonPressed() {
        engine.movePawn("d");
        updateGui();
        logMessage("Moved Down");
    }

    @FXML
    public void onLeftButtonPressed() {
        engine.movePawn("l");
        updateGui();
        logMessage("Moved Left");
    }

    @FXML
    public void onRightButtonPressed() {
        engine.movePawn("r");
        updateGui();
        logMessage("Moved Right");
    }

    @FXML
    public void onRunClicked() {
        engine = new GameEngine(10, 3); // Reset the game with a new engine instance
        updateGui();
        displayTopScores();
        logMessage("Game reset. New dungeon generated.");
    }

    @FXML
    public void onHelpClicked() {
        logMessage("Help: Use the buttons to move your pawn. Interact with tiles to discover their effects. Reach the ladder to advance to the next level.");
    }
    // This method is called when the save button is clicked.
    @FXML
    public void onSaveClicked() {
        logMessage("Save button clicked. Saving game state...");
        engine.saveGame("savefile.txt");
    }

    // This method is called when the load button is clicked.
    @FXML
    public void onLoadClicked() {
        engine.loadGame("savefile.txt");
        updateGui();
        displayTopScores();
        logMessage("Game loaded from savefile.txt.");
    }

    @FXML
    private TextArea topScoresArea;


    private void updateGui() {
        gridPane.getChildren().clear();

        int pawnRow = engine.getPawn().whatsRow();
        int pawnColumn = engine.getPawn().whatsColumn();

        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                ImageView icon;

                if (i == pawnRow && j == pawnColumn) {
                    icon = new ImageView(new Image(getClass().getResourceAsStream("/images/pawn.png")));
                } else {
                    Tile cell = engine.getMap()[i][j];
                    icon = new ImageView(getTileImage(cell));
                }

                icon.setFitWidth(40);
                icon.setFitHeight(40);
                gridPane.add(icon, j, i);
            }
        }

        hpLabel.setText("HP: " + engine.getPawn().whatsHp());
        stepsLabel.setText("Steps: " + engine.getStepsTaken() + " / " + engine.getMaxSteps());
        scoreLabel.setText("Score: " + engine.getPawn().whatsScore());
        gridPane.setGridLinesVisible(true);

        if (!gameOverHandled && engine.getPawn().isGameOver()) {
            int finalScore = engine.getPawn().whatsScore();

            if (engine.getPawn().isGameWon()) {
                logMessage("You escaped the dungeon. Final score: " + finalScore);
                displayTopScores();
                showWinDialog();
            } else {
                logMessage("You died. Final score: " + finalScore);
                displayTopScores();
                showDeathDialog();
            }

            gameOverHandled = true;
        }




    }


    private Image getTileImage(Tile tile) {
        String imageName;

        if (tile instanceof GoldTile) imageName = "gold_tile.png";
        else if (tile instanceof dungeon.engine.ladderTile) imageName = "ladder_tile.png";
        else if (tile instanceof dungeon.engine.TrapTile) imageName = "trap_tile.png";
        else if (tile instanceof dungeon.engine.healthTile) imageName = "health_tile.png";
        else if (tile instanceof dungeon.engine.rangedMutantTile) imageName = "ranged_mutant_tile.png";
        else if (tile instanceof dungeon.engine.meleeMutantTile) imageName = "melee_mutant_tile.png";
        else imageName = "default_tile.png"; // Default tile image

        return new Image(getClass().getResourceAsStream("/images/" + imageName));
    }

    private void showWinDialog() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("You Escaped!");
            alert.setHeaderText("Congratulations!");
            alert.setContentText("You escaped the dungeon!\nFinal score: " + engine.getPawn().whatsScore());

            javafx.scene.control.ButtonType restart = new javafx.scene.control.ButtonType("Restart");
            javafx.scene.control.ButtonType exit = new javafx.scene.control.ButtonType("Exit");
            alert.getButtonTypes().setAll(restart, exit);

            alert.showAndWait().ifPresent(response -> {
                if (response == restart) {
                    engine = new GameEngine(10, 3);
                    engine.loadTopScores();
                    engine.setLogger(this::logMessage);
                    updateGui();
                    gameOverHandled = false;
                    logMessage("Game restarted. New dungeon generated.");
                } else {
                    javafx.application.Platform.exit();
                }
            });
        });
    }

    private void showDeathDialog() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You have been defeated!");
            alert.setContentText("Your final score: " + engine.getPawn().whatsScore());

            javafx.scene.control.ButtonType restart = new javafx.scene.control.ButtonType("Restart");
            javafx.scene.control.ButtonType exit = new javafx.scene.control.ButtonType("Exit");
            alert.getButtonTypes().setAll(restart, exit);

            alert.showAndWait().ifPresent(response -> {
                if (response == restart) {
                    engine = new GameEngine(10, 3);
                    engine.loadTopScores();
                    engine.setLogger(this::logMessage);
                    updateGui();
                    gameOverHandled = false;
                    logMessage("Game restarted. New dungeon generated.");
                } else {
                    javafx.application.Platform.exit();
                }
            });
        });
    }

    private void displayTopScores() {
        List<dungeon.engine.TopScore> scores = engine.getTopScores();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            dungeon.engine.TopScore ts = scores.get(i);
            sb.append(String.format("#%d score: %d   Date: %s\n", i + 1, ts.getScore(), ts.getDate()));
        }
        topScoresArea.setText(sb.toString());
    }


}