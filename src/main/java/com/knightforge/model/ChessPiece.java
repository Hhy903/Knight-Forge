package com.knightforge.model;

/**
 * Immutable chess piece data used by the board state.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessColor color;

    public ChessPiece(PieceType type, ChessColor color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public ChessColor getColor() {
        return color;
    }
}
