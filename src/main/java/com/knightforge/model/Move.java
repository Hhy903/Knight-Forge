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
    private PieceType promotionResult;

    public Move(
            ChessboardPoint from,
            ChessboardPoint to,
            ChessPiece movedPiece,
            ChessPiece capturedPiece,
            ChessboardPoint capturedPiecePoint,
            ChessboardPoint previousEnPassantTarget,
            ChessColor previousCurrentColor
    ) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.capturedPiecePoint = capturedPiecePoint;
        this.previousEnPassantTarget = previousEnPassantTarget;
        this.previousCurrentColor = previousCurrentColor;
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

    public PieceType getPromotionResult() {
        return promotionResult;
    }

    public void setPromotionResult(PieceType promotionResult) {
        this.promotionResult = promotionResult;
    }
}
