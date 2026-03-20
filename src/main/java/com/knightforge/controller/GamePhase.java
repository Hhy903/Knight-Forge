package com.knightforge.controller;

/**
 * Represents the current high-level phase of the chess game.
 */
public enum GamePhase {
    SELECTING_PIECE,
    SELECTING_TARGET,
    PROMOTION_PENDING,
    GAME_OVER,
    LOADING_GAME
}
