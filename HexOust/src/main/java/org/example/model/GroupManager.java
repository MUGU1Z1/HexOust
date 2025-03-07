package org.example.model;

import java.util.*;

/**
 * 管理棋盘上的棋子组
 */
public class GroupManager {
    private final Board board;

    public GroupManager(Board board) {
        this.board = board;
    }

    /**
     * 获取指定位置所在的组
     * @param index 棋子索引
     * @return 包含该位置的所有连接的棋子列表
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

            // 获取所有相邻位置
            List<Integer> adjacentPositions = getAdjacentPositions(currentIndex);

            for (int adjacentIndex : adjacentPositions) {
                Cell adjacentCell = board.getCell(adjacentIndex);
                // 如果相邻单元格有棋子且颜色相同，且未被访问过
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
     * 获取指定位置的所有相邻位置
     * @param index 位置索引
     * @return 相邻位置索引列表
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
     * 将索引转换为二维坐标
     * @param index 索引
     * @return 二维坐标数组[row, col]
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
     * @param row 行
     * @param col 列
     * @return 索引
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