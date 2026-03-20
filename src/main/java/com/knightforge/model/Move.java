package com.knightforge.model;

import com.knightforge.view.ChessboardPoint;

/**
 * Records one move on the board for future undo/save features.
 */
public class Move {
    private final ChessboardPoint from;
    private final ChessboardPoint to;
    private final ChessPiece movedPiece;
    private final ChessPiece capturedPiece;

    public Move(ChessboardPoint from, ChessboardPoint to, ChessPiece movedPiece, ChessPiece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    public ChessboardPoint getFrom() {
        return from;
    }

    public ChessboardPoint getTo() {
        return to;
    }

    public ChessPiece getMovedPiece() {
        return movedPiece;
    }

    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }
}
