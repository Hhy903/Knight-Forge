package com.knightforge.view;

import com.knightforge.controller.ClickController;
import com.knightforge.model.ChessComponent;
import com.knightforge.model.ChessPiece;
import com.knightforge.model.EmptySlotComponent;

import java.awt.*;

/**
 * Factory for creating board square components.
 */
public class PieceComponentFactory {
    public ChessComponent createComponent(
            ChessboardPoint point,
            Point location,
            ChessPiece piece,
            ClickController clickController,
            int size
    ) {
        if (piece == null) {
            return new EmptySlotComponent(point, location, clickController, size);
        }
        return new PieceChessComponent(point, location, piece, clickController, size);
    }
}
