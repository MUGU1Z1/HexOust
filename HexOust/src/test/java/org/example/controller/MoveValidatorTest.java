package org.example.controller;

import javafx.scene.paint.Color;
import org.example.model.Board;
import org.example.model.Player;
import org.example.model.PlayerColor;
import org.example.controller.MoveValidator; // Corrected package path

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List; // Added List import

/**
 * Tests for MoveValidator class functionality
 */
public class MoveValidatorTest {
    private Board board;
    private MoveValidator validator;
    private Player redPlayer;
    private Player bluePlayer;

    @BeforeEach
    void setUp() {
        board = new Board();
        validator = new MoveValidator(board);
        redPlayer = new Player("Red Player", PlayerColor.RED);
        bluePlayer = new Player("Blue Player", PlayerColor.BLUE);
    }

    @Test
    @DisplayName("Test valid move index validation")
    void testValidateMove_ValidIndex() {
        // Valid index range: 0-126
        assertTrue(validator.validateMove(0));
        assertTrue(validator.validateMove(63));
        assertTrue(validator.validateMove(126));
    }

    @Test
    @DisplayName("Test invalid move index validation")
    void testValidateMove_InvalidIndex() {
        // Invalid indices
        assertFalse(validator.validateMove(-1));
        assertFalse(validator.validateMove(127));
        assertFalse(validator.validateMove(1000));
    }

    @Test
    @DisplayName("Test NCP rule - Empty cell, no adjacent same color stones")
    void testValidateNCP_EmptyCellNoAdjacent() {
        // On an empty board, any position should comply with NCP rule
        assertTrue(validator.validateNonCapturingPlacement(redPlayer, 0));
        assertTrue(validator.validateNonCapturingPlacement(bluePlayer, 63));
    }

    @Test
    @DisplayName("Test NCP rule - Occupied cell")
    void testValidateNCP_OccupiedCell() {
        // Place a red stone
        board.placeStone(redPlayer, 0);

        // Trying to place a stone at the same position should fail
        assertFalse(validator.validateNonCapturingPlacement(redPlayer, 0));
        assertFalse(validator.validateNonCapturingPlacement(bluePlayer, 0));
    }

    @Test
    @DisplayName("Test NCP rule - Adjacent to same color stone")
    void testValidateNCP_AdjacentSameColor() {
        // Place a red stone
        board.placeStone(redPlayer, 0);

        // Get adjacent positions
        List<Integer> adjacentPositions = validator.getAdjacentPositions(0);

        // Placing a same color stone at an adjacent position should violate NCP rule
        for (int pos : adjacentPositions) {
            assertFalse(validator.validateNonCapturingPlacement(redPlayer, pos),
                    "Position " + pos + " is adjacent to same color stone, should violate NCP rule");
        }
    }

    @Test
    @DisplayName("Test NCP rule - Adjacent to opponent's stone")
    void testValidateNCP_AdjacentOpponentColor() {
        // Place a blue stone
        board.placeStone(bluePlayer, 0);

        // Get adjacent positions
        List<Integer> adjacentPositions = validator.getAdjacentPositions(0);

        // Red player placing a stone at an adjacent position should comply with NCP rule
        for (int pos : adjacentPositions) {
            assertTrue(validator.validateNonCapturingPlacement(redPlayer, pos),
                    "Position " + pos + " is adjacent to opponent's stone, but should comply with NCP rule");
        }
    }

    @Test
    @DisplayName("Test CP rule - Adjacent to same color stone and opponent's stone")
    void testValidateCP_AdjacentSameColorAndOpponent() {
        // Create a scenario that can trigger CP rule
        board.placeStone(redPlayer, 0);  // Red player's stone
        board.placeStone(bluePlayer, 8);  // Blue player's stone adjacent to red stone

        // Red player places a stone at position 1, which is adjacent to both position 0 and position 8
        int position = 1;

        // Verify position 1 is adjacent to position 0 (red stone)
        List<Integer> adjacentToPos0 = validator.getAdjacentPositions(0);
        assertTrue(adjacentToPos0.contains(position), "Position 1 should be adjacent to position 0");

        // Verify position 1 is adjacent to position 8 (blue stone)
        List<Integer> adjacentToPos8 = validator.getAdjacentPositions(8);
        assertTrue(adjacentToPos8.contains(position), "Position 1 should be adjacent to position 8");

        // Position 1 should comply with CP rule
        assertTrue(validator.validateCapturingPlacement(redPlayer, position));
    }

    @Test
    @DisplayName("Test CP rule - Not adjacent to same color stone")
    void testValidateCP_NoAdjacentSameColor() {
        // Place a blue stone
        board.placeStone(bluePlayer, 63);

        // Red player attempts to place a stone at a non-adjacent position
        int nonAdjacentPosition = 0;  // Assume position 0 is not adjacent to position 63

        // Should not comply with CP rule because it's not adjacent to a red stone
        assertFalse(validator.validateCapturingPlacement(redPlayer, nonAdjacentPosition));
    }

    @Test
    @DisplayName("Test CP rule - Adjacent to same color stone but not to opponent's stone")
    void testValidateCP_AdjacentSameColorNoOpponent() {
        // Place two adjacent red stones
        board.placeStone(redPlayer, 0);

        // Get adjacent positions
        List<Integer> adjacentPositions = validator.getAdjacentPositions(0);
        int adjacentPosition = adjacentPositions.get(0);

        // This position is adjacent to a red stone but not to a blue stone, so it shouldn't comply with CP rule
        assertFalse(validator.validateCapturingPlacement(redPlayer, adjacentPosition));
    }

    @Test
    @DisplayName("Test execute capture - Successful capture of opponent's stone")
    void testExecuteCapture_SuccessfulCapture() {
        // Create a scenario for capture
        // Red group (larger): positions 0 and 1
        board.placeStone(redPlayer, 0);
        board.placeStone(redPlayer, 1);

        // Blue group (smaller): position 8
        board.placeStone(bluePlayer, 8);

        // Red player places a stone at position 9, which is adjacent to positions 1 and 8
        int capturePosition = 9;

        // Execute capture
        List<Integer> capturedPositions = validator.executeCapture(redPlayer, capturePosition);

        // Verify position 8 (blue stone) is captured
        assertTrue(capturedPositions.contains(8), "Position 8 should be captured");
        assertEquals(1, capturedPositions.size(), "There should be only 1 position captured");
    }

    @Test
    @DisplayName("Test execute capture - Group too small to capture")
    void testExecuteCapture_GroupTooSmall() {
        // Create a scenario where capture is not possible
        // Red group (smaller): position 0
        board.placeStone(redPlayer, 0);

        // Blue group (larger): positions 8 and 9
        board.placeStone(bluePlayer, 8);
        board.placeStone(bluePlayer, 9);

        // Red player places a stone at position 1, which is adjacent to positions 0 and 8
        int capturePosition = 1;

        // Execute capture
        List<Integer> capturedPositions = validator.executeCapture(redPlayer, capturePosition);

        // Verify no positions are captured
        assertTrue(capturedPositions.isEmpty(), "No positions should be captured");
    }

    @Test
    @DisplayName("Test getting adjacent positions")
    void testGetAdjacentPositions() {
        // Test the number of adjacent cells at different positions on the board
        // Edge positions should have fewer adjacent cells
        List<Integer> cornerAdjacent = validator.getAdjacentPositions(0);  // Top-left corner
        assertTrue(cornerAdjacent.size() < 6, "Edge positions should have fewer than 6 adjacent cells");

        // Center position should have 6 adjacent cells
        List<Integer> centerAdjacent = validator.getAdjacentPositions(63);  // Assume 63 is the center position
        assertEquals(6, centerAdjacent.size(), "Center position should have 6 adjacent cells");
    }
}