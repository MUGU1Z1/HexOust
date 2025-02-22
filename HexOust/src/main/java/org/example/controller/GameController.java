package org.example.controller;

import org.example.model.Board;
import org.example.model.GameState;
import org.example.model.Player;

public class GameController {
    private final Board board;
    private final GameState gameState;
    private final MoveValidator moveValidator;

    public GameController() {
        this.board = new Board();
        this.gameState = new GameState();
        this.moveValidator = new MoveValidator();
    }

    public boolean makeMove(int index) {
        if (gameState.isGameOver()) {
            System.out.println("Game is over");
            return false;
        }

        // 只需验证索引是否有效
        if (moveValidator.validateMove(index)) {
            Player currentPlayer = gameState.getCurrentPlayer();
            System.out.println("Current player: " + currentPlayer.getName());

            if (board.placeStone(currentPlayer, index)) {
                System.out.println("Stone placed successfully");
                // 只有成功落子后才切换玩家
                gameState.switchPlayer();
                return true;
            } else {
                System.out.println("Failed to place stone");
            }
        } else {
            System.out.println("Invalid move");
        }
        return false;
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

        // 只需验证索引是否有效
        if (moveValidator.validateMove(index)) {
            // 检查是否可以在该位置落子
            return board.canPlaceStone(index);
        }
        return false;
    }

    public Board getBoard() {
        return board;
    }


}