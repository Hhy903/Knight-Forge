package com.knightforge.model.ChessPieces;

import com.knightforge.model.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece {
    ChessColor color;
    String type;

    public ChessPiece(ChessColor color, String name) {
        this.color = color;
        this.type = name;
    }

    public ChessColor getColor() { return this.color; }
    public String getType() { return this.type; }

    public List<ChessboardPosition> getPossibleMoves(ChessboardPosition currentPosition, ChessPiece[][] chessboard, List<MoveNew> moveHistory) {
        List<ChessboardPosition> legalMoves = new ArrayList<>();

        for (int row = 0; row < chessboard.length; row++) {
            for (int col = 0; col < chessboard[0].length; col++) {
                ChessboardPosition target = new ChessboardPosition(row, col);
                if (isLegalMove(currentPosition, target, chessboard)) {
                    legalMoves.add(target);
                }
            }
        }

        legalMoves.addAll(getPossibleSpecialMoves(currentPosition, chessboard, moveHistory));
        return legalMoves;
    }

    public boolean isLegalMove(ChessboardPosition from, ChessboardPosition to, ChessPiece[][] chessboard) {
        return (!from.equals(to) &&
                movementDirectionIsValid(from, to) &&
                pathIsClear(from, to, chessboard) &&
                finalPositionIsValid(to, chessboard));
    }

    abstract protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to);

    protected boolean pathIsClear(ChessboardPosition from, ChessboardPosition to, ChessPiece[][] chessboard) {
        int rowStep = Integer.signum(to.getX() - from.getX());
        int colStep = Integer.signum(to.getY() - from.getY());
        int row = from.getX() + rowStep;
        int col = from.getY() + colStep;

        while (row != to.getX() || col != to.getY()) {
            if (chessboard[row][col] != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    protected boolean finalPositionIsValid(ChessboardPosition to, ChessPiece[][] chessboard) {
        ChessPiece targetPiece = chessboard[to.getX()][to.getY()];
        return targetPiece == null || targetPiece.getColor() != this.color;
    }

    public List<ChessboardPosition> getPossibleSpecialMoves(ChessboardPosition position, ChessPiece[][] board, List<MoveNew> moveHistory){ return new ArrayList<>(); }
}
