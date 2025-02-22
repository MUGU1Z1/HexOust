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

    public GameView() {
        controller = new GameController();

        // 创建红色和蓝色的圆形示例
        redColorCircle = new Circle(10, Color.RED);
        blueColorCircle = new Circle(10, Color.BLUE);

        // 创建 "Turn" 标签
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

        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER);

        playerNameLabel = new Label();
        playerNameLabel.setStyle("-fx-font-size: 16;");

        statusBox.getChildren().addAll(playerNameLabel, turnLabel);

        topBox.getChildren().addAll(titleLabel, statusBox);
        root.setTop(topBox);

        boardView = new BoardView(controller);

        // 添加玩家变化监听器
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

        // 初始更新状态标签
        updateStatusLabel(controller.getCurrentPlayer());
    }

    private void updateStatusLabel(Player currentPlayer) {
        Platform.runLater(() -> {
            if (currentPlayer.getColor() == PlayerColor.RED) {
                playerNameLabel.setTextFill(Color.RED);
                playerNameLabel.setGraphic(redColorCircle);
            } else {
                playerNameLabel.setTextFill(Color.BLUE);
                playerNameLabel.setGraphic(blueColorCircle);
            }

            playerNameLabel.setText(currentPlayer.getName());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}