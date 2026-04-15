package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    static String PIECE_NAME = "KING";

    public King(ChessColor color) {
        super(color, PIECE_NAME);
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return (dx <= 1 && dy <= 1);
    }
}
