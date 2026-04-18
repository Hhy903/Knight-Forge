package com.knightforge.view.ViewComponents;

import com.knightforge.model.GameState;

public interface UpdatableUIComponent {

    private void addComponent(UpdatableUIComponent component) {}

    abstract void updateGameState(GameState state);
}