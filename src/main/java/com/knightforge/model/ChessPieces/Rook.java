package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {
    static String PIECE_NAME = "Rook";

    public Rook(ChessColor color) {
        super(color, PIECE_NAME);
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return (from.getX() == to.getX() || from.getY() == to.getY());
    }
}