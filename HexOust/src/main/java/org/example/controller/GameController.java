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
            System.out.println("Index validation passed");

            // Try to perform NCP operation (not adjacent to own stones)
            if (moveValidator.validateNonCapturingPlacement(currentPlayer, index)) {
                System.out.println("NCP rule validation passed, attempting to place stone");
                if (board.placeStone(currentPlayer, index)) {
                    System.out.println("Stone placed successfully in NCP mode");
                    if (continueCaptureMode) {
                        System.out.println("Performing NCP operation in continuous capture mode, ending current turn");
                    }
                    endCaptureMode();

                    // Check if game is over
                    checkGameStatus();

                    return true;
                } else {
                    System.out.println("Failed to place stone in NCP mode");
                }
            }
            else if (moveValidator.validateCapturingPlacement(currentPlayer, index)) {
                System.out.println("CP rule validation passed, attempting to place stone and capture");
                boolean result = performCapture(currentPlayer, index);

                // Check if game is over
                checkGameStatus();

                return result;
            }
            else {
                System.out.println("Invalid position: does not comply with NCP or CP rules");
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
                    System.out.println("Removed captured stone: " + capturedPos + " coordinates: [" + coordinates[0] + "," + coordinates[1] + "]");
                }
            }

            if (!capturedPositions.isEmpty()) {
                continueCaptureMode = true;
                System.out.println("Continuous capture allowed");
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

    // Skip capture turn method removed

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

    /**
     * Check if the player has legal moves
     * @param player The player to check
     * @return true if the player has legal moves
     */
    public boolean hasLegalMoves(Player player) {
        // Check all possible cell positions
        for (int index = 0; index < 127; index++) {
            if (moveValidator.validateMove(index)) {
                if (!board.canPlaceStone(index)) {
                    continue;
                }

                // Check NCP rule
                if (moveValidator.validateNonCapturingPlacement(player, index)) {
                    return true;
                }

                // Check CP rule
                if (moveValidator.validateCapturingPlacement(player, index)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the player has stones on the board
     * @param player The player to check
     * @return true if the player has stones
     */
    public boolean hasStones(Player player) {
        for (int index = 0; index < 127; index++) {
            org.example.model.Cell cell = board.getCell(index);
            if (cell != null && cell.isOccupied() && cell.getOccupiedBy() == player) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check game status and decide if the game is over
     */
    public void checkGameStatus() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Player opponent = (currentPlayer == gameState.getPlayer1()) ?
                gameState.getPlayer2() : gameState.getPlayer1();

        // Check if opponent has stones
        if (!hasStones(opponent)) {
            // If opponent has no stones, current player wins
            gameState.setGameOver(true);
            gameState.setWinner(currentPlayer);
            System.out.println(currentPlayer.getName() + " wins! Opponent has no stones left.");
            return;
        }

        // Check if current player has legal moves
        boolean currentPlayerHasLegalMoves = hasLegalMoves(currentPlayer);
        boolean opponentHasLegalMoves = hasLegalMoves(opponent);

        if (!currentPlayerHasLegalMoves && !opponentHasLegalMoves) {
            // If both players have no legal moves, game is over, player with more stones wins
            gameState.setGameOver(true);
            int currentPlayerStones = countStones(currentPlayer);
            int opponentStones = countStones(opponent);

            if (currentPlayerStones >= opponentStones) {
                // If current player has more or equal stones, current player wins
                gameState.setWinner(currentPlayer);
                System.out.println(currentPlayer.getName() + " wins! Has more or equal stones.");
            } else {
                // Otherwise opponent wins
                gameState.setWinner(opponent);
                System.out.println(opponent.getName() + " wins! Has more stones.");
            }
        } else if (!currentPlayerHasLegalMoves) {
            // Current player has no legal moves, opponent wins
            gameState.setGameOver(true);
            gameState.setWinner(opponent);
            System.out.println(opponent.getName() + " wins! Current player has no legal moves.");
        } else if (!opponentHasLegalMoves && !continueCaptureMode) {
            // Opponent has no legal moves, current player wins
            // Note: Only end game immediately if not in continuous capture mode
            gameState.setGameOver(true);
            gameState.setWinner(currentPlayer);
            System.out.println(currentPlayer.getName() + " wins! Opponent has no legal moves.");
        }
    }

    /**
     * Count player's stones
     * @param player The player to count stones for
     * @return Number of stones
     */
    private int countStones(Player player) {
        int count = 0;
        for (int index = 0; index < 127; index++) {
            org.example.model.Cell cell = board.getCell(index);
            if (cell != null && cell.isOccupied() && cell.getOccupiedBy() == player) {
                count++;
            }
        }
        return count;
    }
}