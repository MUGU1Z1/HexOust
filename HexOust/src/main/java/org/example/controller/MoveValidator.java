package org.example.controller;

import javafx.scene.paint.Color;
import org.example.model.Board;
import org.example.model.Cell;
import org.example.model.Player;
import org.example.model.PlayerColor;
import org.example.model.GroupManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveValidator {
    private final Board board;
    private final GroupManager groupManager;

    public MoveValidator(Board board) {
        this.board = board;
        this.groupManager = new GroupManager(board);
    }

    /**
     * Validate if the index is within the valid range
     */
    public boolean validateMove(int index) {
        // Validate if the index is within the valid range (total of 127 cells)
        return index >= 0 && index < 127;
    }

    /**
     * Validate NCP rule: A stone cannot be adjacent to another stone of the same color,
     * it can only be adjacent to opponent's stones or placed independently
     */
    public boolean validateNonCapturingPlacement(Player currentPlayer, int index) {
        // First check if the position is empty
        Cell targetCell = board.getCell(index);
        if (targetCell == null || targetCell.isOccupied()) {
            return false;
        }

        System.out.println("Checking NCP rule: index " + index);

        // Get all adjacent cells
        List<Cell> adjacentCells = getAdjacentCells(index);
        System.out.println("Found " + adjacentCells.size() + " adjacent cells");

        // Check if there are any same-colored stones adjacent
        Color playerColor = currentPlayer.getColor() == PlayerColor.RED ? Color.RED : Color.BLUE;
        System.out.println("Current player color: " + playerColor);

        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell != null && adjacentCell.isOccupied()) {
                System.out.println("Adjacent cell has a stone, color: " + adjacentCell.getStoneColor());
                // Check if the stone color is the same as the current player
                if (adjacentCell.getStoneColor().equals(playerColor)) {
                    System.out.println("Found adjacent stone with the same color as player, NCP rule validation failed");
                    return false; // If there's a same-colored stone adjacent, it doesn't comply with NCP rule
                }
            }
        }

        System.out.println("NCP rule validation passed");
        // If there are no same-colored stones adjacent, it complies with NCP rule
        return true;
    }

    /**
     * Validate CP rule: Stone must be adjacent to player's own stones, forming a larger group,
     * and the new group must be adjacent to at least one opponent's stone
     */
    public boolean validateCapturingPlacement(Player player, int index) {
        System.out.println("Validating CP rule: position " + index);

        // First check if the position is empty
        Cell targetCell = board.getCell(index);
        if (targetCell == null || targetCell.isOccupied()) {
            System.out.println("Position is null or already occupied");
            return false;
        }

        // Check if there are any player's stones adjacent
        List<Integer> adjacentPlayerPositions = getAdjacentPlayerPositions(player, index);
        if (adjacentPlayerPositions.isEmpty()) {
            System.out.println("No stones of current player adjacent");
            return false;
        }

        // Get the new group that would be formed after placement
        Set<Integer> newGroup = simulateNewGroup(player, index, adjacentPlayerPositions);

        // Check if the new group is adjacent to at least one opponent's stone
        List<List<Integer>> adjacentOpponentGroups = getAdjacentOpponentGroups(newGroup, player.getColor());
        if (adjacentOpponentGroups.isEmpty()) {
            System.out.println("New group is not adjacent to any opponent stones");
            return false;
        }

        System.out.println("CP rule validation passed");
        return true;
    }

    /**
     * Execute capture logic
     */
    public List<Integer> executeCapture(Player player, int index) {
        List<Integer> capturedPositions = new ArrayList<>();

        // Get adjacent player stone positions
        List<Integer> adjacentPlayerPositions = getAdjacentPlayerPositions(player, index);
        if (adjacentPlayerPositions.isEmpty()) {
            return capturedPositions;
        }

        // Simulate the new group that would be formed after placement
        Set<Integer> newGroup = simulateNewGroup(player, index, adjacentPlayerPositions);

        // Get adjacent opponent groups
        List<List<Integer>> adjacentOpponentGroups = getAdjacentOpponentGroups(newGroup, player.getColor());

        // Check if capture is possible
        int newGroupSize = newGroup.size();
        for (List<Integer> opponentGroup : adjacentOpponentGroups) {
            if (newGroupSize > opponentGroup.size()) {
                // Capture this opponent group
                capturedPositions.addAll(opponentGroup);
            }
        }

        return capturedPositions;
    }

    /**
     * Get adjacent cells
     */
    private List<Cell> getAdjacentCells(int index) {
        List<Cell> adjacentCells = new ArrayList<>();

        // Convert index to 2D coordinates
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates == null) {
            return adjacentCells;
        }

        int row = coordinates[0];
        int col = coordinates[1];

        System.out.println("Getting adjacent cells, coordinates: [" + row + ", " + col + "]");

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
            System.out.println("Using first six rows direction offsets");
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
            System.out.println("Using seventh row direction offsets");
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
            System.out.println("Using last six rows direction offsets");
        }

        // Check adjacent cells in all directions
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            Cell cell = board.getCell(newRow, newCol);
            if (cell != null) {
                System.out.println("Found adjacent cell, coordinates: [" + newRow + ", " + newCol + "]");
                adjacentCells.add(cell);
            }
        }

        return adjacentCells;
    }

    /**
     * Get positions of adjacent player stones
     */
    private List<Integer> getAdjacentPlayerPositions(Player player, int index) {
        List<Integer> adjacentPlayerPositions = new ArrayList<>();
        List<Integer> adjacentPositions = getAdjacentPositions(index);

        Color playerColor = player.getColor() == PlayerColor.RED ? Color.RED : Color.BLUE;

        for (int adjacentPos : adjacentPositions) {
            Cell cell = board.getCell(adjacentPos);
            if (cell != null && cell.isOccupied() && cell.getStoneColor().equals(playerColor)) {
                adjacentPlayerPositions.add(adjacentPos);
            }
        }

        return adjacentPlayerPositions;
    }

    /**
     * Simulate the new group that would be formed after stone placement
     */
    private Set<Integer> simulateNewGroup(Player player, int index, List<Integer> adjacentPlayerPositions) {
        Set<Integer> newGroup = new HashSet<>();
        newGroup.add(index);

        // Get all groups containing the adjacent player stones
        for (int pos : adjacentPlayerPositions) {
            List<Integer> group = groupManager.getGroup(pos);
            newGroup.addAll(group);
        }

        return newGroup;
    }

    /**
     * Get all opponent stone groups adjacent to the specified group
     */
    private List<List<Integer>> getAdjacentOpponentGroups(Set<Integer> group, PlayerColor playerColor) {
        Set<Integer> checkedOpponentPositions = new HashSet<>();
        List<List<Integer>> opponentGroups = new ArrayList<>();

        for (int position : group) {
            List<Integer> adjacentPositions = getAdjacentPositions(position);

            for (int adjacentPos : adjacentPositions) {
                Cell adjacentCell = board.getCell(adjacentPos);
                // Check if it's an opponent's stone and hasn't been checked before
                if (adjacentCell != null && adjacentCell.isOccupied()
                        && !isSamePlayerColor(adjacentCell, playerColor)
                        && !checkedOpponentPositions.contains(adjacentPos)) {

                    // Found an opponent group
                    List<Integer> opponentGroup = groupManager.getGroup(adjacentPos);
                    opponentGroups.add(opponentGroup);

                    // Mark all positions in this group as checked
                    checkedOpponentPositions.addAll(opponentGroup);
                }
            }
        }

        return opponentGroups;
    }

    /**
     * Get all adjacent positions for a specified position
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
     * Check if the stone color in the cell is the same as the specified player color
     */
    private boolean isSamePlayerColor(Cell cell, PlayerColor playerColor) {
        if (playerColor == PlayerColor.RED) {
            return cell.getStoneColor().equals(Color.RED);
        } else {
            return cell.getStoneColor().equals(Color.BLUE);
        }
    }

    /**
     * Convert index to 2D coordinates
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