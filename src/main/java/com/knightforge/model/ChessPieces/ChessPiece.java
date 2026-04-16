package com.knightforge.model.ChessPieces;

import com.knightforge.model.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece {
    ChessColor color;
    PieceType type;

    public ChessPiece(ChessColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public ChessColor getColor() { return this.color; }
    public PieceType getType() { return this.type; }

    public List<MoveNew> getPossibleMoves(ChessboardPosition currentPosition, Chessboard chessboard, List<MoveNew> moveHistory) {
        List<MoveNew> legalMoves = new ArrayList<>();

        for (int row = 0; row < chessboard.getHeight(); row++) {
            for (int col = 0; col < chessboard.getLength(); col++) {
                ChessboardPosition target = new ChessboardPosition(row, col);
                if (isLegalMove(currentPosition, target, chessboard)) {
                    legalMoves.add(new MoveNew(currentPosition, target, chessboard.getPieceAtPosition(currentPosition), chessboard.getPieceAtPosition(target)));
                }
            }
        }

        legalMoves.addAll(getPossibleSpecialMoves(currentPosition, chessboard, moveHistory));

        return legalMoves;
    }

    public boolean isLegalMove(ChessboardPosition from, ChessboardPosition to, Chessboard chessboard) {
        return (!from.equals(to) &&
                movementDirectionIsValid(from, to) &&
                pathIsClear(from, to, chessboard) &&
                finalPositionIsValid(to, chessboard));
    }

    abstract protected boolean movementDirectionIsValid(ChessboardPosition from, ChessboardPosition to);

    protected boolean pathIsClear(ChessboardPosition from, ChessboardPosition to, Chessboard chessboard) {
        int rowStep = Integer.signum(to.getX() - from.getX());
        int colStep = Integer.signum(to.getY() - from.getY());
        int row = from.getX() + rowStep;
        int col = from.getY() + colStep;

        while (row != to.getX() || col != to.getY()) {
            if (chessboard.getPieceAtPosition(new ChessboardPosition(row, col)) != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    protected boolean finalPositionIsValid(ChessboardPosition to, Chessboard chessboard) {
        ChessPiece targetPiece = chessboard.getPieceAtPosition(to);
        return targetPiece == null || targetPiece.getColor() != this.color;
    }

    public List<MoveNew> getPossibleSpecialMoves(ChessboardPosition position, Chessboard board, List<MoveNew> moveHistory){ return new ArrayList<>(); }
}
