package com.knightforge.model;

public enum GameMode {
    IDLE("Waiting for piece selection"),
    STAGING("Piece selected - choose destination"),
    AWAITING_PROMOTION("Choose promotion piece"),
    GAME_OVER("Game over");

    private final String description;

    GameMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
