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
     * 验证索引是否在有效范围内
     */
    public boolean validateMove(int index) {
        // 验证索引是否在有效范围内（总共127个单元格）
        return index >= 0 && index < 127;
    }

    /**
     * 验证NCP规则：棋子不能与自己的棋子相邻，只能与对手的棋子相邻或独立放置
     */
    public boolean validateNonCapturingPlacement(Player currentPlayer, int index) {
        // 首先检查该位置是否为空
        Cell targetCell = board.getCell(index);
        if (targetCell == null || targetCell.isOccupied()) {
            return false;
        }

        System.out.println("检查NCP规则: 索引 " + index);

        // 获取相邻的所有单元格
        List<Cell> adjacentCells = getAdjacentCells(index);
        System.out.println("找到 " + adjacentCells.size() + " 个相邻单元格");

        // 检查是否有自己的棋子相邻
        Color playerColor = currentPlayer.getColor() == PlayerColor.RED ? Color.RED : Color.BLUE;
        System.out.println("当前玩家颜色: " + playerColor);

        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell != null && adjacentCell.isOccupied()) {
                System.out.println("相邻格有棋子，颜色: " + adjacentCell.getStoneColor());
                // 检查棋子颜色是否与当前玩家相同
                if (adjacentCell.getStoneColor().equals(playerColor)) {
                    System.out.println("发现与玩家颜色相同的相邻棋子，NCP规则验证失败");
                    return false; // 如果有自己的棋子相邻，则不符合NCP规则
                }
            }
        }

        System.out.println("NCP规则验证通过");
        // 如果没有自己的棋子相邻，则符合NCP规则
        return true;
    }

    /**
     * 验证CP规则：棋子必须与自己的棋子相邻，形成更大的组，且新组必须至少与一个对手棋子相邻
     */
    public boolean validateCapturingPlacement(Player player, int index) {
        System.out.println("验证CP规则: 位置 " + index);

        // 首先检查该位置是否为空
        Cell targetCell = board.getCell(index);
        if (targetCell == null || targetCell.isOccupied()) {
            System.out.println("位置为null或已被占用");
            return false;
        }

        // 检查相邻位置是否有当前玩家的棋子
        List<Integer> adjacentPlayerPositions = getAdjacentPlayerPositions(player, index);
        if (adjacentPlayerPositions.isEmpty()) {
            System.out.println("没有与当前玩家相邻的棋子");
            return false;
        }

        // 获取放置后会形成的新组
        Set<Integer> newGroup = simulateNewGroup(player, index, adjacentPlayerPositions);

        // 检查新组是否与至少一个对手棋子相邻
        List<List<Integer>> adjacentOpponentGroups = getAdjacentOpponentGroups(newGroup, player.getColor());
        if (adjacentOpponentGroups.isEmpty()) {
            System.out.println("新组没有与对手棋子相邻");
            return false;
        }

        System.out.println("CP规则验证通过");
        return true;
    }

    /**
     * 执行捕获逻辑
     */
    public List<Integer> executeCapture(Player player, int index) {
        List<Integer> capturedPositions = new ArrayList<>();

        // 获取相邻的玩家棋子位置
        List<Integer> adjacentPlayerPositions = getAdjacentPlayerPositions(player, index);
        if (adjacentPlayerPositions.isEmpty()) {
            return capturedPositions;
        }

        // 模拟放置棋子后形成的新组
        Set<Integer> newGroup = simulateNewGroup(player, index, adjacentPlayerPositions);

        // 获取相邻的对手组
        List<List<Integer>> adjacentOpponentGroups = getAdjacentOpponentGroups(newGroup, player.getColor());

        // 检查是否可以捕获
        int newGroupSize = newGroup.size();
        for (List<Integer> opponentGroup : adjacentOpponentGroups) {
            if (newGroupSize > opponentGroup.size()) {
                // 捕获这个对手组
                capturedPositions.addAll(opponentGroup);
            }
        }

        return capturedPositions;
    }

    /**
     * 获取相邻的单元格
     */
    private List<Cell> getAdjacentCells(int index) {
        List<Cell> adjacentCells = new ArrayList<>();

        // 将索引转换为二维坐标
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates == null) {
            return adjacentCells;
        }

        int row = coordinates[0];
        int col = coordinates[1];

        System.out.println("获取相邻单元格，坐标: [" + row + ", " + col + "]");

        // 根据列的位置确定相邻方向
        int[][] directions;

        // 根据列的位置选择不同的方向数组
        if (row < 6) {
            // 前六列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {-1, -1}, // 左上
                    {0, 1},   // 右上
                    {0, -1},  // 左下
                    {1, 1},   // 右下
                    {1, 0}    // 正下
            };
            System.out.println("使用前六列的方向偏移");
        } else if (row == 6) {
            // 第七列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {-1, -1}, // 左上
                    {-1, 1},  // 右上
                    {0, -1},  // 左下
                    {0, 1},   // 右下
                    {1, 0}    // 正下
            };
            System.out.println("使用第七列的方向偏移");
        } else {
            // 后六列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {0, -1},  // 左上
                    {-1, 1},  // 右上
                    {1, -1},  // 左下
                    {0, 1},   // 右下
                    {1, 0}    // 正下
            };
            System.out.println("使用后六列的方向偏移");
        }

        // 检查所有方向的相邻单元格
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            Cell cell = board.getCell(newRow, newCol);
            if (cell != null) {
                System.out.println("找到相邻单元格，坐标: [" + newRow + ", " + newCol + "]");
                adjacentCells.add(cell);
            }
        }

        return adjacentCells;
    }

    /**
     * 获取相邻的玩家棋子位置
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
     * 模拟放置棋子后会形成的新组
     */
    private Set<Integer> simulateNewGroup(Player player, int index, List<Integer> adjacentPlayerPositions) {
        Set<Integer> newGroup = new HashSet<>();
        newGroup.add(index);

        // 获取所有相邻玩家棋子所在的组
        for (int pos : adjacentPlayerPositions) {
            List<Integer> group = groupManager.getGroup(pos);
            newGroup.addAll(group);
        }

        return newGroup;
    }

    /**
     * 获取与指定组相邻的所有对手棋子组
     */
    private List<List<Integer>> getAdjacentOpponentGroups(Set<Integer> group, PlayerColor playerColor) {
        Set<Integer> checkedOpponentPositions = new HashSet<>();
        List<List<Integer>> opponentGroups = new ArrayList<>();

        for (int position : group) {
            List<Integer> adjacentPositions = getAdjacentPositions(position);

            for (int adjacentPos : adjacentPositions) {
                Cell adjacentCell = board.getCell(adjacentPos);
                // 检查是否是对手的棋子，且之前没有检查过
                if (adjacentCell != null && adjacentCell.isOccupied()
                        && !isSamePlayerColor(adjacentCell, playerColor)
                        && !checkedOpponentPositions.contains(adjacentPos)) {

                    // 找到对手的一个组
                    List<Integer> opponentGroup = groupManager.getGroup(adjacentPos);
                    opponentGroups.add(opponentGroup);

                    // 标记这个组中的所有位置为已检查
                    checkedOpponentPositions.addAll(opponentGroup);
                }
            }
        }

        return opponentGroups;
    }

    /**
     * 获取指定位置的所有相邻位置
     */
    public List<Integer> getAdjacentPositions(int index) {
        List<Integer> adjacentPositions = new ArrayList<>();
        int[] coordinates = convertIndexToCoordinates(index);
        if (coordinates == null) {
            return adjacentPositions;
        }

        int row = coordinates[0];
        int col = coordinates[1];

        // 根据列的位置确定相邻方向
        int[][] directions;

        // 根据列的位置选择不同的方向数组
        if (row < 6) {
            // 前六列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {-1, -1}, // 左上
                    {0, 1},   // 右上
                    {0, -1},  // 左下
                    {1, 1},   // 右下
                    {1, 0}    // 正下
            };
        } else if (row == 6) {
            // 第七列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {-1, -1}, // 左上
                    {-1, 1},  // 右上
                    {0, -1},  // 左下
                    {0, 1},   // 右下
                    {1, 0}    // 正下
            };
        } else {
            // 后六列
            directions = new int[][]{
                    {-1, 0},  // 正上
                    {0, -1},  // 左上
                    {-1, 1},  // 右上
                    {1, -1},  // 左下
                    {0, 1},   // 右下
                    {1, 0}    // 正下
            };
        }

        // 检查所有方向的相邻单元格
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (board.getCell(newRow, newCol) != null) {
                // 将坐标转换回索引
                int adjacentIndex = convertCoordinatesToIndex(newRow, newCol);
                if (adjacentIndex >= 0) {
                    adjacentPositions.add(adjacentIndex);
                }
            }
        }

        return adjacentPositions;
    }

    /**
     * 检查单元格中的棋子颜色是否与指定玩家颜色相同
     */
    private boolean isSamePlayerColor(Cell cell, PlayerColor playerColor) {
        if (playerColor == PlayerColor.RED) {
            return cell.getStoneColor().equals(Color.RED);
        } else {
            return cell.getStoneColor().equals(Color.BLUE);
        }
    }

    /**
     * 将索引转换为二维坐标
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
     * 将二维坐标转换为索引
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