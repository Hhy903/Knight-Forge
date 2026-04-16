package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends ChessPiece {
    public Bishop(ChessColor color) {
        super(color, PieceType.BISHOP);
    }

    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY());
    }
}
