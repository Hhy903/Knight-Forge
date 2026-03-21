package com.knightforge.controller;

/**
 * Observer interface for session state changes.
 */
public interface GameSessionListener {
    void onSessionChanged(GameSessionEvent event);
}
