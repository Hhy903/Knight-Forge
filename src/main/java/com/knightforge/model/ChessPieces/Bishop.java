package com.knightforge.model.ChessPieces;

import com.knightforge.model.ChessColor;
import com.knightforge.model.ChessboardPosition;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends ChessPiece {
    static String PIECE_NAME = "BISHOP";

    public Bishop(ChessColor color) {
        super(color, PIECE_NAME);
    }

//    @Override
//    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition currentPosition, ChessPiece[][] chessboard) {
//        List<ChessboardPosition> legalMoves = new ArrayList<>();
//
//        for (int row = 0; row < chessboard.length; row++) {
//            for (int col = 0; col < chessboard[0].length; col++) {
//                ChessboardPosition target = new ChessboardPosition(row, col);
//                if (isLegalMove(currentPosition, target, chessboard)) {
//                    legalMoves.add(target);
//                }
//            }
//        }
//
//        return legalMoves;
//    }
//
//    public boolean isLegalMove(ChessboardPosition from, ChessboardPosition to, ChessPiece[][] chessboard) {
//        return (!from.equals(to) &&
//                movementDirectionIsValid(from, to) &&
//                pathIsClear(from, to, chessboard) &&
//                finalPositionIsValid(to, chessboard));
//    }

    protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to) {
        return Math.abs(from.getX() - to.getX()) == Math.abs(from.getY() - to.getY());
    }
//
//    private boolean pathIsClear(ChessboardPosition from, ChessboardPosition to, ChessPiece[][] chessboard) {
//        int rowStep = Integer.compare(to.getX(), from.getX());
//        int colStep = Integer.compare(to.getY(), from.getY());
//        int row = from.getX() + rowStep;
//        int col = from.getY() + colStep;
//
//        while (row != to.getX() || col != to.getY()) {
//            if (chessboard[row][col] != null) {
//                return false;
//            }
//            row += rowStep;
//            col += colStep;
//        }
//        return true;
//    }
//
//    private boolean finalPositionIsValid(ChessboardPosition to, ChessPiece[][] chessboard) {
//        ChessPiece targetPiece = chessboard[to.getX()][to.getY()];
//        return targetPiece == null || targetPiece.getColor() != this.color;
//    }

//    private boolean wouldLeaveKingInCheck(ChessboardPosition from, ChessboardPosition to) {
//        ChessPiece movingPiece = getPieceAt(from);
//        ChessPiece capturedPiece = getPieceAt(to);
//        ChessboardPosition capturedPoint = copyPoint(to);
//        boolean enPassantCapture = isEnPassantCapture(from, to, movingPiece);
//        if (enPassantCapture) {
//            capturedPoint = new ChessboardPosition(from.getX(), to.getY());
//            capturedPiece = getPieceAt(capturedPoint);
//            board[capturedPoint.getX()][capturedPoint.getY()] = null;
//        }
//        board[to.getX()][to.getY()] = movingPiece;
//        board[from.getX()][from.getY()] = null;
//
//        ChessboardPosition kingPoint = movingPiece.getType() == PieceType.KING ? to : findKing(movingPiece.getColor());
//        boolean inCheck = kingPoint == null || isSquareUnderAttack(kingPoint, oppositeColor(movingPiece.getColor()));
//
//        board[from.getX()][from.getY()] = movingPiece;
//        board[to.getX()][to.getY()] = capturedPoint.equals(to) ? capturedPiece : null;
//        if (enPassantCapture && capturedPoint != null) {
//            board[capturedPoint.getX()][capturedPoint.getY()] = capturedPiece;
//        }
//        return inCheck;
//    }
}
