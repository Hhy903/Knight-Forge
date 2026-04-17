package com.knightforge.model;

import java.util.List;

public interface IMoveHandler {
    public List<MoveNew> getValidMoves(ChessColor whoseTurn, ChessboardPosition position);
    public void executeMove(MoveNew move) throws PromotionRequiredException;
    public void executePromotionMove(MoveNew move, PieceType desiredPiece);
    public boolean undoLastMove();
}
