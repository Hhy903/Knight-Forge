package com.knightforge.controller;

/**
 * Immutable event snapshot published by GameSession.
 */
public class GameSessionEvent {
    private final GamePhase phase;
    private final String statusMessage;
    private final String gameResult;

    public GameSessionEvent(GamePhase phase, String statusMessage, String gameResult) {
        this.phase = phase;
        this.statusMessage = statusMessage;
        this.gameResult = gameResult;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getGameResult() {
        return gameResult;
    }
}
