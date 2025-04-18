package org.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.controller.GameController;
import org.example.model.Player;
import org.example.model.PlayerColor;

public class GameView extends Application {
    private GameController controller;
    private BoardView boardView;
    private Label playerNameLabel;
    private final Label turnLabel;
    private final Circle redColorCircle;
    private final Circle blueColorCircle;
    private Label captureLabel;
    private boolean gameOverDisplayed = false; // Add a flag to track game over display status

    public GameView() {
        controller = new GameController();

        redColorCircle = new Circle(10, Color.RED);
        blueColorCircle = new Circle(10, Color.BLUE);

        turnLabel = new Label("Turn");
        turnLabel.setStyle("-fx-font-size: 16;");
    }

    @Override
    public void start(Stage primaryStage) {
        initializeUI(primaryStage);
    }

    private void initializeUI(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("HexOust Game");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        StackPane statusPane = new StackPane();
        statusPane.setAlignment(Pos.CENTER);

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER);

        playerNameLabel = new Label();
        playerNameLabel.setStyle("-fx-font-size: 16;");

        turnLabel.setStyle("-fx-font-size: 16;");

        captureLabel = new Label("Continuous Capture Mode");
        captureLabel.setStyle("-fx-font-size: 14; -fx-text-fill: red;");
        captureLabel.setVisible(false);

        statusBox.getChildren().addAll(playerNameLabel, turnLabel);
        statusPane.getChildren().add(statusBox);

        topBox.getChildren().addAll(titleLabel, statusPane);
        root.setTop(topBox);

        boardView = new BoardView(controller);

        // Add game over listener before player change listener
        controller.getGameState().addGameOverListener(this::showGameOver);

        // Add player change listener
        controller.getGameState().addPlayerChangeListener(this::updateStatusLabel);

        StackPane gameArea = new StackPane(boardView);
        gameArea.setPadding(new Insets(10));
        gameArea.setPrefSize(1200, 1000);
        root.setCenter(gameArea);

        Scene scene = new Scene(root, 1200, 1000);
        stage.setTitle("HexOust Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.centerOnScreen();

        // Initial status update
        updateStatusLabel(controller.getCurrentPlayer());

        // Add game state update timer with lower frequency to prevent overriding
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), event -> {
                    // Check if game is over
                    if (controller.getGameState().isGameOver() && !gameOverDisplayed) {
                        Player winner = controller.getGameState().getWinner();
                        if (winner != null) {
                            showGameOver(winner);
                        }
                    } else if (!controller.getGameState().isGameOver()) {
                        updateCaptureUI();
                    }
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Display game over information
     * @param winner The winner
     */
    private void showGameOver(Player winner) {
        Platform.runLater(() -> {
            // Set game over information
            if (winner != null) {
                String winnerText = winner.getColor() == PlayerColor.RED ? "Red Player Wins" : "Blue Player Wins";

                // Clear previous content
                playerNameLabel.setGraphic(null);

                // Set Game Over and victory text
                playerNameLabel.setText("Game Over");
                turnLabel.setText(winnerText);

                // Set victory text color to winner's color
                playerNameLabel.setTextFill(Color.BLACK);
                if (winner.getColor() == PlayerColor.RED) {
                    turnLabel.setTextFill(Color.RED);
                } else {
                    turnLabel.setTextFill(Color.BLUE);
                }

                // Mark game over as displayed
                gameOverDisplayed = true;

                System.out.println("Game Over UI updated: " + playerNameLabel.getText() + " " + turnLabel.getText());
            }

            // Hide capture mode label
            captureLabel.setVisible(false);
        });
    }

    /**
     * Update capture UI status
     */
    private void updateCaptureUI() {
        Platform.runLater(() -> {
            // If game is over or game over is displayed, don't update UI
            if (controller.getGameState().isGameOver() || gameOverDisplayed) {
                return;
            }

            boolean isCaptureMode = controller.isContinueCaptureMode();
            if (isCaptureMode) {
                if (getCurrentPlayer().getColor() == PlayerColor.RED) {
                    playerNameLabel.setText("Red Player");
                    turnLabel.setText("Continuous Capture Mode");
                } else {
                    playerNameLabel.setText("Blue Player");
                    turnLabel.setText("Continuous Capture Mode");
                }
            } else {
                if (getCurrentPlayer().getColor() == PlayerColor.RED) {
                    playerNameLabel.setText("Red Player");
                    turnLabel.setText("Turn");
                } else {
                    playerNameLabel.setText("Blue Player");
                    turnLabel.setText("Turn");
                }
            }

            captureLabel.setVisible(false);
        });
    }

    private Player getCurrentPlayer() {
        return controller.getCurrentPlayer();
    }

    private void updateStatusLabel(Player currentPlayer) {
        Platform.runLater(() -> {
            // If game is over or game over is displayed, don't update status label
            if (controller.getGameState().isGameOver() || gameOverDisplayed) {
                return;
            }

            if (currentPlayer.getColor() == PlayerColor.RED) {
                playerNameLabel.setTextFill(Color.RED);
                playerNameLabel.setGraphic(redColorCircle);
                playerNameLabel.setText("Red Player");
            } else {
                playerNameLabel.setTextFill(Color.BLUE);
                playerNameLabel.setGraphic(blueColorCircle);
                playerNameLabel.setText("Blue Player");
            }

            // updateCaptureUI will be called in the timer, so no need to call it here
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}