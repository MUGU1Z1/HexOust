package org.example.model;

public class Board {
    private final Cell[][] grid;
    private final int SIZE = 13;  // Changed to 13 to match actual board size

    public Board() {
        grid = new Cell[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < totalHexesPerRow[row]; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    public Cell getCell(int x, int y) {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

        if (x >= 0 && x < SIZE && y >= 0 && y < totalHexesPerRow[x]) {
            return grid[x][y];
        }
        return null;
    }

    public boolean placeStone(Player player, int x, int y) {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

        if (x >= 0 && x < SIZE && y >= 0 && y < totalHexesPerRow[x] && getCell(x, y) != null && !getCell(x, y).isOccupied()) {
            getCell(x, y).setStone(player);
            return true;
        }
        return false;
    }

    public boolean placeStone(Player player, int index) {
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates != null) {
            return placeStone(player, coordinates[0], coordinates[1]);
        }
        return false;
    }

    public boolean removeStone(int x, int y) {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

        if (x >= 0 && x < SIZE && y >= 0 && y < totalHexesPerRow[x]) {
            getCell(x, y).removeStone();
            return true;
        }
        return false;
    }

    private int[] convertIndexToCoordinates(int index) {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};
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

    public int getSize() {
        return SIZE;
    }

    public boolean canPlaceStone(int index) {
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates != null) {
            Cell cell = getCell(coordinates[0], coordinates[1]);
            return cell != null && !cell.isOccupied();
        }
        return false;
    }

    public Cell getCell(int index) {
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates != null) {
            return getCell(coordinates[0], coordinates[1]);
        }
        return null;
    }
}