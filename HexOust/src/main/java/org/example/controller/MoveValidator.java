package org.example.controller;

import org.example.model.Board;

public class MoveValidator {
    public boolean validateMove(int index) {
        // 验证索引是否在有效范围内（总共127个单元格）
        return index >= 0 && index < 127;
    }
}