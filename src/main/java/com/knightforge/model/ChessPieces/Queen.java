package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;
import com.knightforge.model.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece {

    public Queen(ChessColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return (from.getX() == to.getX() || from.getY() == to.getY()) ||
                (Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY()));
    }
}
