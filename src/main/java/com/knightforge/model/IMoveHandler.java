package com.knightforge.model;

import java.util.List;

public interface IMoveHandler {
    public List<ChessboardPosition> getValidMoves(ChessColor whoseTurn, ChessboardPosition position);
}
