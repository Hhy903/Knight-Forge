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
    private final ChessboardPoint capturedPiecePoint;
    private final ChessboardPoint previousEnPassantTarget;
    private final ChessColor previousCurrentColor;
    private final boolean previousWhiteKingSideCastleAvailable;
    private final boolean previousWhiteQueenSideCastleAvailable;
    private final boolean previousBlackKingSideCastleAvailable;
    private final boolean previousBlackQueenSideCastleAvailable;
    private final ChessboardPoint rookFrom;
    private final ChessboardPoint rookTo;
    private PieceType promotionResult;

    public Move(
            ChessboardPoint from,
            ChessboardPoint to,
            ChessPiece movedPiece,
            ChessPiece capturedPiece,
            ChessboardPoint capturedPiecePoint,
            ChessboardPoint previousEnPassantTarget,
            ChessColor previousCurrentColor,
            boolean previousWhiteKingSideCastleAvailable,
            boolean previousWhiteQueenSideCastleAvailable,
            boolean previousBlackKingSideCastleAvailable,
            boolean previousBlackQueenSideCastleAvailable,
            ChessboardPoint rookFrom,
            ChessboardPoint rookTo
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
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
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

    public ChessboardPoint getCapturedPiecePoint() {
        return capturedPiecePoint;
    }

    public ChessboardPoint getPreviousEnPassantTarget() {
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

    public ChessboardPoint getRookFrom() {
        return rookFrom;
    }

    public ChessboardPoint getRookTo() {
        return rookTo;
    }

    public PieceType getPromotionResult() {
        return promotionResult;
    }

    public void setPromotionResult(PieceType promotionResult) {
        this.promotionResult = promotionResult;
    }
}
