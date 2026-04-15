package com.knightforge.model;

import com.knightforge.model.ChessPiece;

/**
 * Records one move on the board for future undo/save features.
 */
public class Move {
    private final ChessboardPosition from;
    private final ChessboardPosition to;
    private final ChessPiece movedPiece;
    private final ChessPiece capturedPiece;
    private final ChessboardPosition capturedPiecePoint;
    private final ChessboardPosition previousEnPassantTarget;
    private final ChessColor previousCurrentColor;
    private final boolean previousWhiteKingSideCastleAvailable;
    private final boolean previousWhiteQueenSideCastleAvailable;
    private final boolean previousBlackKingSideCastleAvailable;
    private final boolean previousBlackQueenSideCastleAvailable;
    private final int previousHalfmoveClock;
    private final ChessboardPosition rookFrom;
    private final ChessboardPosition rookTo;
    private PieceType promotionResult;

    public Move(
            ChessboardPosition from,
            ChessboardPosition to,
            ChessPiece movedPiece,
            ChessPiece capturedPiece,
            ChessboardPosition capturedPiecePoint,
            ChessboardPosition previousEnPassantTarget,
            ChessColor previousCurrentColor,
            boolean previousWhiteKingSideCastleAvailable,
            boolean previousWhiteQueenSideCastleAvailable,
            boolean previousBlackKingSideCastleAvailable,
            boolean previousBlackQueenSideCastleAvailable,
            int previousHalfmoveClock,
            ChessboardPosition rookFrom,
            ChessboardPosition rookTo
    ) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.capturedPiecePoint = capturedPiecePoint;
        this.previousEnPassantTarget = previousEnPassantTarget;
        this.previousCurrentColor = previousCurrentColor;
        this.previousWhiteKingSideCastleAvailable = previousWhiteKingSideCastleAvailable;
        this.previousWhiteQueenSideCastleAvailable = previousWhiteQueenSideCastleAvailable;
        this.previousBlackKingSideCastleAvailable = previousBlackKingSideCastleAvailable;
        this.previousBlackQueenSideCastleAvailable = previousBlackQueenSideCastleAvailable;
        this.previousHalfmoveClock = previousHalfmoveClock;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
    }

    public ChessboardPosition getFrom() {
        return from;
    }

    public ChessboardPosition getTo() {
        return to;
    }

    public ChessPiece getMovedPiece() {
        return movedPiece;
    }

    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }

    public ChessboardPosition getCapturedPiecePoint() {
        return capturedPiecePoint;
    }

    public ChessboardPosition getPreviousEnPassantTarget() {
        return previousEnPassantTarget;
    }

    public ChessColor getPreviousCurrentColor() {
        return previousCurrentColor;
    }

    public boolean isPreviousWhiteKingSideCastleAvailable() {
        return previousWhiteKingSideCastleAvailable;
    }

    public boolean isPreviousWhiteQueenSideCastleAvailable() {
        return previousWhiteQueenSideCastleAvailable;
    }

    public boolean isPreviousBlackKingSideCastleAvailable() {
        return previousBlackKingSideCastleAvailable;
    }

    public boolean isPreviousBlackQueenSideCastleAvailable() {
        return previousBlackQueenSideCastleAvailable;
    }

    public int getPreviousHalfmoveClock() {
        return previousHalfmoveClock;
    }

    public ChessboardPosition getRookFrom() {
        return rookFrom;
    }

    public ChessboardPosition getRookTo() {
        return rookTo;
    }

    public PieceType getPromotionResult() {
        return promotionResult;
    }

    public void setPromotionResult(PieceType promotionResult) {
        this.promotionResult = promotionResult;
    }
}
