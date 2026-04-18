package com.knightforge.model;

import java.util.List;

public interface IMoveHandler {
    public List<Move> getValidMoves(ChessColor whoseTurn, ChessboardPosition position);
    public void executeMove(Move move) throws PromotionRequiredException;
    public void executePromotionMove(Move move, PieceType desiredPiece);
    public boolean undoLastMove();
}
