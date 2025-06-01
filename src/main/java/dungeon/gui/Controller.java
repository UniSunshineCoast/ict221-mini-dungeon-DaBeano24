package dungeon.gui;

import dungeon.engine.GoldTile;
import dungeon.engine.Tile;
import dungeon.engine.GameEngine;
import javafx.fxml.FXML;
//import javafx.geometry.Insets;
import javafx.scene.control.Label;
//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {

    private void logMessage(String message) {
        gameLog.appendText(message + "\n");
    }

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
        updateGui();
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
        logMessage("Game reset. New dungeon generated.");
    }

    @FXML
    public void onHelpClicked() {
        logMessage("Help: Use the buttons to move your pawn. Interact with tiles to discover their effects. Reach the ladder to advance to the next level.");
    }

    @FXML
    public void onSaveClicked() {
        // Implement save functionality here
        logMessage("Save functionality is not yet implemented.");
    }

    private void updateGui() {
        gridPane.getChildren().clear();
        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                Tile cell = engine.getMap()[i][j];
                ImageView icon = new ImageView(getTileImage(cell));
                icon.setFitWidth(40);
                icon.setFitHeight(40);
                gridPane.add(icon, j, i); // j equals colomn (x). and i equals row (y).
            }
        }

        hpLabel.setText("HP: " + engine.getPawn().whatsHp());
        stepsLabel.setText("Steps: " + engine.getPawn().whatsSteps());
        scoreLabel.setText("Score: " + engine.getPawn().whatsScore());
        gridPane.setGridLinesVisible(true);
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



}
