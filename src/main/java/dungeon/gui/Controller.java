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
                gridPane.add(icon, j, i); // j = column, i = row
            }
        }

        hpLabel.setText("HP: " + engine.getPawn().whatsHp());
        stepsLabel.setText("Steps: " + engine.getStepsTaken() + " / " + engine.getMaxSteps());
        scoreLabel.setText("Score: " + engine.getPawn().whatsScore());
        gridPane.setGridLinesVisible(true);

if (!gameOverHandled) {
    if (engine.getLevel() > 2 && !gameOverHandled) {
        int finalScore = engine.getPawn().whatsScore();

        if (engine.updateTopScores(finalScore)) {
            logMessage("Congratulations! You escaped the dungeon and made the top scores list with a score of " + finalScore + "!");
        } else {
            logMessage("You have escaped the dungeon, Your final score is: " + finalScore);
        }

        displayTopScores();
        showGameOverDialog("win");
        gameOverHandled = true;

    } else if (engine.getPawn().isGameOver() && !gameOverHandled) {
        int finalScore = engine.getPawn().whatsScore();

        if (engine.updateTopScores(finalScore)) {
            logMessage("Congratulations! You made the top scores list with a score of " + finalScore + "!");
        }

        displayTopScores();
        showGameOverDialog("lose");
        gameOverHandled = true;
    }
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

    private void showGameOverDialog(String outcome) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");

            if (outcome.equals("win")) {
                alert.setHeaderText("Congratulations! You escaped the dungeon!");
                alert.setContentText("Your final score is: " + engine.getPawn().whatsScore());
            } else {
                alert.setHeaderText("You have been defeated!");
                alert.setContentText("Would you like to restart or exit?");
            }

            javafx.scene.control.ButtonType restartButton = new javafx.scene.control.ButtonType("Restart");
            javafx.scene.control.ButtonType exitButton = new javafx.scene.control.ButtonType("Exit");

            alert.getButtonTypes().setAll(restartButton, exitButton);
            alert.showAndWait().ifPresent(response -> {
                if (response == restartButton) {
                    engine = new GameEngine(10, 3); // Reset the game with a new engine instance
                    engine.loadTopScores();
                    engine.setLogger(this::logMessage);
                    updateGui();
                    logMessage("Game restarted. New dungeon generated.");
                } else if (response == exitButton) {
                    javafx.application.Platform.exit(); // Exit the application
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
