package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.Chessboard;
import com.knightforge.model.ChessboardPosition;

public class Knight extends ChessPiece {
    static String PIECE_NAME = "KNIGHT";
    public Knight(ChessColor color) {
        super(color, PIECE_NAME);
    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return dx * dy == 2;
    }

    @Override
    protected boolean pathIsClear(ChessboardPosition from, ChessboardPosition to, Chessboard chessboard) {
        return true;
    }
}
