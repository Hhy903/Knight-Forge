package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece {
    static String PIECE_NAME = "Queen";

    public Queen(ChessColor color) {
        super(color, PIECE_NAME);
    }

//    @Override
//    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition currentPosition, ChessPiece[][] chessboard) {
//        List<ChessboardPosition> legalMoves = new ArrayList<>();
//        return legalMoves;
//    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return (from.getX() == to.getX() || from.getY() == to.getY()) ||
                (Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY()));
    }
}
