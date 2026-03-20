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
        super(chessboardPoint, location, ChessColor.NONE, listener, size);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination) {
        return false;
    }

    @Override
    public void loadResource() throws IOException {
        // No resource required.
    }

}
