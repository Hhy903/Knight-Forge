package com.knightforge.model;

import java.util.Objects;

/**
 * Represents a square on the chessboard, such as (0, 0) or (7, 7).
 */
public class ChessboardPosition {
    private int x, y;

    public ChessboardPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessboardPosition that)) {
            return false;
        }
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ") " + "on the chessboard is clicked!";
    }
}
