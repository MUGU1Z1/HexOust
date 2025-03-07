package org.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        // 使用已存在的 turnLabel，而不是创建新的
        // 但可以修改其样式
        turnLabel.setStyle("-fx-font-size: 16;");

        // 创建捕获模式标签 - 这个标签现在保留但默认不显示
        captureLabel = new Label("Continuous Capture Mode");
        captureLabel.setStyle("-fx-font-size: 14; -fx-text-fill: red;");
        captureLabel.setVisible(false);

        // 只添加玩家标签和回合标签，使用固定布局
        statusBox.getChildren().addAll(playerNameLabel, turnLabel);
        statusPane.getChildren().add(statusBox);

        topBox.getChildren().addAll(titleLabel, statusPane);
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

        // 添加游戏状态更新计时器
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100), event -> {
                    updateCaptureUI();
                })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * 更新捕获UI状态
     */
    private void updateCaptureUI() {
        Platform.runLater(() -> {
            boolean isCaptureMode = controller.isContinueCaptureMode();
            if (isCaptureMode) {
                // 连续捕获模式下的提示
                if (getCurrentPlayer().getColor() == PlayerColor.RED) {
                    playerNameLabel.setText("Red Player");
                    turnLabel.setText("Continuous Capture Mode");
                } else {
                    playerNameLabel.setText("Blue Player");
                    turnLabel.setText("Continuous Capture Mode");
                }
            } else {
                // 普通模式下的提示
                if (getCurrentPlayer().getColor() == PlayerColor.RED) {
                    playerNameLabel.setText("Red Player");
                    turnLabel.setText("Turn");
                } else {
                    playerNameLabel.setText("Blue Player");
                    turnLabel.setText("Turn");
                }
            }

            // 隐藏额外的捕获模式标签，我们现在使用turnLabel显示模式信息
            captureLabel.setVisible(false);
        });
    }

    // 辅助方法，获取当前玩家
    private Player getCurrentPlayer() {
        return controller.getCurrentPlayer();
    }

    private void updateStatusLabel(Player currentPlayer) {
        Platform.runLater(() -> {
            if (currentPlayer.getColor() == PlayerColor.RED) {
                playerNameLabel.setTextFill(Color.RED);
                playerNameLabel.setGraphic(redColorCircle);
                playerNameLabel.setText("Red Player");
            } else {
                playerNameLabel.setTextFill(Color.BLUE);
                playerNameLabel.setGraphic(blueColorCircle);
                playerNameLabel.setText("Blue Player");
            }

            // 在这里不设置turnLabel的文本，让updateCaptureUI处理
            updateCaptureUI();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}