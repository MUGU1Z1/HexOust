package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Player currentPlayer;
    private final Player player1;
    private final Player player2;
    private boolean gameOver;
    private Player winner; // Add winner property

    public interface PlayerChangeListener {
        void onPlayerChanged(Player currentPlayer);
    }

    public interface GameOverListener {
        void onGameOver(Player winner);
    }

    private List<PlayerChangeListener> playerListeners = new ArrayList<>();
    private List<GameOverListener> gameOverListeners = new ArrayList<>();

    public GameState() {
        this.player1 = new Player("Red Player", PlayerColor.RED);
        this.player2 = new Player("Blue Player", PlayerColor.BLUE);
        this.currentPlayer = player1;  // Explicitly set Red player to go first
        this.gameOver = false;
        this.winner = null;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void addPlayerChangeListener(PlayerChangeListener listener) {
        playerListeners.add(listener);
    }

    public void addGameOverListener(GameOverListener listener) {
        gameOverListeners.add(listener);
    }

    private void notifyPlayerListeners() {
        for (PlayerChangeListener listener : playerListeners) {
            listener.onPlayerChanged(currentPlayer);
        }
    }

    private void notifyGameOverListeners() {
        for (GameOverListener listener : gameOverListeners) {
            listener.onGameOver(winner);
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        notifyPlayerListeners();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver && winner != null) {
            System.out.println("Notifying game over listeners: Winner is " + winner.getName());
            notifyGameOverListeners();
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    /**
     * Set the game winner
     * @param winner The winner
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Get the game winner
     * @return The winner, or null if game is not over
     */
    public Player getWinner() {
        return winner;
    }
}