package com.knightforge.model;

/**
 * Enumerates the supported chess piece types.
 */
public enum PieceType {
    KING("king"),
    QUEEN("queen"),
    ROOK("rook"),
    BISHOP("bishop"),
    KNIGHT("knight"),
    PAWN("pawn");

    private final String resourceName;

    PieceType(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
