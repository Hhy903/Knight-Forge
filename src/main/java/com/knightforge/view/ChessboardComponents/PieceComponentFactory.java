package com.knightforge.view.ChessboardComponents;

import com.knightforge.controller.ClickController;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.model.ChessboardPosition;

import java.awt.*;

/**
 * Factory for creating board square components.
 */
public class PieceComponentFactory {
    public ChessComponent createComponent(
            ChessboardPosition point,
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
