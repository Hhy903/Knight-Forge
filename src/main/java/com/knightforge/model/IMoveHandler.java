package com.knightforge.model;

import java.util.List;

public interface IMoveHandler {
    public List<MoveNew> getValidMoves(ChessColor whoseTurn, ChessboardPosition position);
    public boolean executeMove(MoveNew move);
    public boolean undoMove(MoveNew move);
}
