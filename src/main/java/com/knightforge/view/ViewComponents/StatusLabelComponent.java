package com.knightforge.view.ViewComponents;

import com.knightforge.model.GameState;

import javax.swing.*;
import java.awt.*;

public class StatusLabelComponent extends JLabel implements UpdatableUIComponent {
    public StatusLabelComponent(int height) {
        setLocation(height, height / 10);
        setSize(220, 100);
        setVerticalAlignment(SwingConstants.TOP);
        setFont(new Font("Rockwell", Font.BOLD, 18));
    }

    private void updateStatus(String statusMessage) {
        setText("<html>" + (statusMessage == null ? "White to move." : statusMessage) + "</html>");
    }

    @Override
    public void updateGameState(GameState state) {
        updateStatus(state.statusMessage());
    }
}
