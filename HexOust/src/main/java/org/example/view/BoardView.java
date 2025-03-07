package org.example.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.example.controller.GameController;
import org.example.model.Cell;
import org.example.model.Player;
import org.example.model.PlayerColor;

public class BoardView extends Pane {
    private final GameController controller;
    private static final double HEX_SIZE = 25;
    private Polygon lastHighlightedHexagon = null;

    public BoardView(GameController controller) {
        this.controller = controller;
        setStyle("-fx-background-color: white;");
        createHexagonalBoard();
    }

    public void refresh() {
        getChildren().clear();
        createHexagonalBoard();
    }

    private void createHexagonalBoard() {
        double horizontalDistance = HEX_SIZE * 1.5;
        double verticalDistance = HEX_SIZE * Math.sqrt(3);

        double boardWidth = 13 * horizontalDistance;
        double boardHeight = 13 * verticalDistance;

        double startX = (1200 - boardWidth) / 2;
        double startY = (1000 - boardHeight) / 2 - 50;

        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};
        int cellIndex = 0;

        for (int col = 0; col < totalHexesPerRow.length; col++) {
            int hexCount = totalHexesPerRow[col];
            double yOffset = (13 - hexCount) * verticalDistance / 2;

            for (int row = 0; row < hexCount; row++) {
                double x = startX + col * horizontalDistance;
                double y = startY + yOffset + row * verticalDistance;

                Polygon hexagon = createHexagon(x, y);
                hexagon.setUserData(cellIndex);

                setupHexagonEvents(hexagon);

                getChildren().add(hexagon);
                cellIndex++;
            }
        }
    }

    private void setupHexagonEvents(Polygon hexagon) {
        hexagon.setOnMouseEntered(event -> handleMouseEnter(hexagon));
        hexagon.setOnMouseExited(event -> handleMouseExit(hexagon));
        hexagon.setOnMouseClicked(event -> handleCellClick(hexagon));
    }

    private void updateCellColor(int index) {
        Cell cell = controller.getBoard().getCell(index);
        Polygon hexagon = (Polygon) getChildren().get(index);

        if (cell != null && cell.isOccupied()) {
            Color cellColor = cell.getStoneColor();
            hexagon.setFill(cellColor);
        } else {
            hexagon.setFill(Color.WHITE);
        }
    }

    private void handleMouseEnter(Polygon hexagon) {
        int index = (Integer) hexagon.getUserData();

        if (controller.canPlaceStone(index)) {
            if (lastHighlightedHexagon != null) {
                int lastIndex = (Integer) lastHighlightedHexagon.getUserData();
                updateCellColor(lastIndex);
            }

            // 根据可能的放置类型使用不同的高亮
            Player currentPlayer = controller.getCurrentPlayer();
            // 检查是否符合NCP规则
            if (new org.example.controller.MoveValidator(controller.getBoard()).validateNonCapturingPlacement(currentPlayer, index)) {
                // NCP模式下使用黄色高亮
                hexagon.setFill(Color.YELLOW);
            } else {
                // CP模式下使用绿色高亮
                hexagon.setFill(Color.LIGHTGREEN);
            }

            lastHighlightedHexagon = hexagon;
        }
    }

    private void handleMouseExit(Polygon hexagon) {
        if (hexagon == lastHighlightedHexagon) {
            int index = (Integer) hexagon.getUserData();
            updateCellColor(index);
            lastHighlightedHexagon = null;
        }
    }

    private void handleCellClick(Polygon hexagon) {
        int index = (Integer) hexagon.getUserData();

        if (controller.makeMove(index)) {
            // 更新被点击的单元格颜色
            updateCellColor(index);

            // 更新所有棋子的显示
            for (int i = 0; i < getChildren().size(); i++) {
                updateCellColor(i);
            }

            // 重置高亮状态
            if (lastHighlightedHexagon != null) {
                int lastIndex = (Integer) lastHighlightedHexagon.getUserData();
                updateCellColor(lastIndex);
                lastHighlightedHexagon = null;
            }
        }
    }

    private Polygon createHexagon(double x, double y) {
        Polygon hexagon = new Polygon();
        double angle = 0;  // 修改为0，使六边形顺时针旋转30度

        for (int i = 0; i < 6; i++) {
            double pointX = x + HEX_SIZE * Math.cos(angle + i * Math.PI / 3);
            double pointY = y + HEX_SIZE * Math.sin(angle + i * Math.PI / 3);
            hexagon.getPoints().addAll(pointX, pointY);
        }

        hexagon.setFill(Color.WHITE);
        hexagon.setStroke(Color.BLACK);
        return hexagon;
    }
}