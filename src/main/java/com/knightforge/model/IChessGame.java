package com.knightforge.model;

import com.knightforge.model.ChessPieces.ChessPiece;

import java.util.List;

public interface IChessGame {

    public abstract void switchTurns();

    public abstract List<MoveNew> getAllPossibleMoves(ChessboardPosition currentPosition);

    public abstract boolean executeMove(MoveNew move);

    public abstract void undoLastMove();

    public abstract List<ChessboardPosition> getLocationsOfPiece(PieceType type, ChessColor color);

    public abstract ChessPiece[][] getBoardState();
}
