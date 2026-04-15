package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    static String PIECE_NAME = "King";
    public King(ChessColor color) {super(color, PIECE_NAME); }

//    //TODO
//    @Override
//    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition currentPosition, ChessPiece[][] chessboard) {
//        List<ChessboardPosition> legalMoves = new ArrayList<>();
//        return legalMoves;
//    }

    @Override
    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        return (dx <= 1 && dy <= 1);
//        return (dx <= 1 && dy <= 1) || canCastle(from, to);
    }
    @Override
    protected boolean pathIsClear(ChessboardPosition from, ChessboardPosition to, ChessPiece[][] chessboard) {
        return true;
    }
}
