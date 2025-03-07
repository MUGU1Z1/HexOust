package org.example.controller;

import org.example.model.Board;
import org.example.model.GameState;
import org.example.model.Player;

import java.util.List;

public class GameController {
    private final Board board;
    private final GameState gameState;
    private final MoveValidator moveValidator;

    private boolean continueCaptureMode = false;

    private int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

    public GameController() {
        this.board = new Board();
        this.gameState = new GameState();
        this.moveValidator = new MoveValidator(board);
    }

    public boolean makeMove(int index) {
        if (gameState.isGameOver()) {
            System.out.println("Game is over");
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        System.out.println("Current player: " + currentPlayer.getName());

        if (moveValidator.validateMove(index)) {
            System.out.println("索引验证通过");

            // 尝试进行NCP操作 (不与自己的棋子相邻)
            if (moveValidator.validateNonCapturingPlacement(currentPlayer, index)) {
                System.out.println("NCP规则验证通过，尝试放置棋子");
                if (board.placeStone(currentPlayer, index)) {
                    System.out.println("Stone placed successfully in NCP mode");
                    if (continueCaptureMode) {
                        System.out.println("在连续捕获模式下进行NCP操作，结束当前回合");
                    }
                    endCaptureMode();
                    return true;
                } else {
                    System.out.println("Failed to place stone in NCP mode");
                }
            }
            else if (moveValidator.validateCapturingPlacement(currentPlayer, index)) {
                System.out.println("CP规则验证通过，尝试放置棋子并捕获");
                return performCapture(currentPlayer, index);
            }
            else {
                System.out.println("位置无效: 不符合NCP或CP规则");
            }
        } else {
            System.out.println("Invalid move");
        }
        return false;
    }

    private boolean performCapture(Player currentPlayer, int index) {
        if (board.placeStone(currentPlayer, index)) {
            System.out.println("Stone placed successfully in CP mode");

            List<Integer> capturedPositions = moveValidator.executeCapture(currentPlayer, index);

            for (int capturedPos : capturedPositions) {
                int[] coordinates = convertIndexToCoordinates(capturedPos);
                if (coordinates != null) {
                    board.removeStone(coordinates[0], coordinates[1]);
                    System.out.println("移除被捕获的棋子: " + capturedPos + " 坐标: [" + coordinates[0] + "," + coordinates[1] + "]");
                }
            }

            if (!capturedPositions.isEmpty()) {
                continueCaptureMode = true;
                System.out.println("允许继续捕获");
            } else {
                endCaptureMode();
            }

            return true;
        } else {
            System.out.println("Failed to place stone in CP mode");
            return false;
        }
    }

    private void endCaptureMode() {
        continueCaptureMode = false;
        gameState.switchPlayer();
    }

    public Player getCurrentPlayer() {
        return gameState.getCurrentPlayer();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean canPlaceStone(int index) {
        if (gameState.isGameOver()) {
            return false;
        }

        if (moveValidator.validateMove(index)) {
            if (!board.canPlaceStone(index)) {
                return false;
            }

            Player currentPlayer = getCurrentPlayer();
            return moveValidator.validateNonCapturingPlacement(currentPlayer, index) ||
                    moveValidator.validateCapturingPlacement(currentPlayer, index);
        }
        return false;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isContinueCaptureMode() {
        return continueCaptureMode;
    }

    public boolean skipCaptureTurn() {
        if (continueCaptureMode) {
            endCaptureMode();
            return true;
        }
        return false;
    }

    private int[] convertIndexToCoordinates(int index) {
        int currentIndex = 0;

        for (int row = 0; row < totalHexesPerRow.length; row++) {
            for (int col = 0; col < totalHexesPerRow[row]; col++) {
                if (currentIndex == index) {
                    return new int[]{row, col};
                }
                currentIndex++;
            }
        }

        return null;
    }
}