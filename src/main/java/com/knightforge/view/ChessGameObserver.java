package com.knightforge.view;

import com.knightforge.model.GameState;

public interface ChessGameObserver {
    public void updateGameState(GameState gameState);
}
