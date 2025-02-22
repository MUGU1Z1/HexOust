package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Player currentPlayer;
    private final Player player1;
    private final Player player2;
    private boolean gameOver;

    public interface PlayerChangeListener {
        void onPlayerChanged(Player currentPlayer);
    }

    private List<PlayerChangeListener> listeners = new ArrayList<>();

    public GameState() {
        this.player1 = new Player("Red Player", PlayerColor.RED);
        this.player2 = new Player("Blue Player", PlayerColor.BLUE);
        this.currentPlayer = player1;  // 明确设置红色玩家为先手
        this.gameOver = false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void addPlayerChangeListener(PlayerChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (PlayerChangeListener listener : listeners) {
            listener.onPlayerChanged(currentPlayer);
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        notifyListeners();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}