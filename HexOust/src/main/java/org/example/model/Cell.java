package org.example.model;

import javafx.scene.paint.Color;

public class Cell {
    private Player occupiedBy;
    private final int x;
    private final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupiedBy = null;
    }

    public boolean isOccupied() {
        return occupiedBy != null;
    }

    public void setStone(Player player) {
        this.occupiedBy = player;
    }

    public void removeStone() {
        this.occupiedBy = null;
    }

    public Player getOccupiedBy() {
        return occupiedBy;
    }

    // 添加这个方法，用于获取当前格子的颜色
    public Color getStoneColor() {
        if (occupiedBy == null) {
            System.out.println("Cell is not occupied");
            return Color.WHITE;
        }
        System.out.println("Occupied by player: " + occupiedBy.getName());
        System.out.println("Player color: " + occupiedBy.getColor());
        Color color = occupiedBy.getColor() == PlayerColor.RED ? Color.RED : Color.BLUE;
        System.out.println("Returning color: " + color);
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


}