package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {

    public Rook(ChessColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return (from.getX() == to.getX() || from.getY() == to.getY());
    }
}