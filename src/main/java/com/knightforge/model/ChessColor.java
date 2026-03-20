package com.knightforge.model;

import java.awt.*;

/**
 * Wraps the display color and label used by the chess game.
 */
public enum ChessColor {
    BLACK("Black", Color.BLACK), WHITE("White", Color.WHITE), NONE("No Player", Color.WHITE);

    private final String name;
    private final Color color;

    ChessColor(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
