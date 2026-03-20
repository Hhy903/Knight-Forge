package com.knightforge.model;

import com.knightforge.controller.ClickController;
import com.knightforge.view.ChessboardPoint;

import java.awt.*;
import java.io.IOException;

/**
 * Represents an empty square on the board.
 */
public class EmptySlotComponent extends ChessComponent {

    public EmptySlotComponent(ChessboardPoint chessboardPoint, Point location, ClickController listener, int size) {
        super(chessboardPoint, location, listener, size);
    }

    @Override
    public ChessColor getChessColor() {
        return ChessColor.NONE;
    }

    @Override
    public boolean canMoveTo(BoardState boardState, ChessboardPoint destination) {
        return false;
    }

    @Override
    public void loadResource() throws IOException {
        // No resource required.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isMoveHint()) {
            g.setColor(new Color(46, 204, 113, 170));
            g.fillOval(getWidth() / 3, getHeight() / 3, getWidth() / 3, getHeight() / 3);
        }
        if (isSelected()) {
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
