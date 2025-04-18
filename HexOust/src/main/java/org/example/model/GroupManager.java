package org.example.model;

import java.util.*;

/**
 * Manages stone groups on the board
 */
public class GroupManager {
    private final Board board;

    public GroupManager(Board board) {
        this.board = board;
    }

    /**
     * Gets the group that contains the specified position
     * @param index Stone index
     * @return List of all connected stones in the same group
     */
    public List<Integer> getGroup(int index) {
        Cell cell = board.getCell(index);
        if (cell == null || !cell.isOccupied()) {
            return new ArrayList<>();
        }

        List<Integer> group = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(index);
        visited.add(index);

        while (!queue.isEmpty()) {
            int currentIndex = queue.poll();
            group.add(currentIndex);

            // Get all adjacent positions
            List<Integer> adjacentPositions = getAdjacentPositions(currentIndex);

            for (int adjacentIndex : adjacentPositions) {
                Cell adjacentCell = board.getCell(adjacentIndex);
                // If adjacent cell has a stone of the same color and hasn't been visited
                if (adjacentCell != null && adjacentCell.isOccupied()
                        && adjacentCell.getStoneColor().equals(cell.getStoneColor())
                        && !visited.contains(adjacentIndex)) {
                    queue.add(adjacentIndex);
                    visited.add(adjacentIndex);
                }
            }
        }

        return group;
    }

    /**
     * Gets all adjacent positions for a specified position
     * @param index Position index
     * @return List of adjacent position indices
     */
    public List<Integer> getAdjacentPositions(int index) {
        List<Integer> adjacentPositions = new ArrayList<>();
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates == null) {
            return adjacentPositions;
        }

        int row = coordinates[0];
        int col = coordinates[1];

        // Determine adjacent directions based on row position
        int[][] directions;

        // Choose different direction arrays based on row position
        if (row < 6) {
            // First six rows
            directions = new int[][]{
                    {-1, 0},  // Up
                    {-1, -1}, // Upper left
                    {0, 1},   // Upper right
                    {0, -1},  // Lower left
                    {1, 1},   // Lower right
                    {1, 0}    // Down
            };
        } else if (row == 6) {
            // Seventh row
            directions = new int[][]{
                    {-1, 0},  // Up
                    {-1, -1}, // Upper left
                    {-1, 1},  // Upper right
                    {0, -1},  // Lower left
                    {0, 1},   // Lower right
                    {1, 0}    // Down
            };
        } else {
            // Last six rows
            directions = new int[][]{
                    {-1, 0},  // Up
                    {0, -1},  // Upper left
                    {-1, 1},  // Upper right
                    {1, -1},  // Lower left
                    {0, 1},   // Lower right
                    {1, 0}    // Down
            };
        }

        // Check adjacent cells in all directions
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (board.getCell(newRow, newCol) != null) {
                // Convert coordinates back to index
                int adjacentIndex = convertCoordinatesToIndex(newRow, newCol);
                if (adjacentIndex >= 0) {
                    adjacentPositions.add(adjacentIndex);
                }
            }
        }

        return adjacentPositions;
    }

    /**
     * Convert index to 2D coordinates
     * @param index Index
     * @return 2D coordinate array [row, col]
     */
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

    /**
     * Convert 2D coordinates to index
     * @param row Row
     * @param col Column
     * @return Index
     */
    private int convertCoordinatesToIndex(int row, int col) {
        int[] totalHexesPerRow = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

        if (row < 0 || row >= totalHexesPerRow.length || col < 0 || col >= totalHexesPerRow[row]) {
            return -1;
        }

        int index = 0;
        for (int r = 0; r < row; r++) {
            index += totalHexesPerRow[r];
        }

        return index + col;
    }
}