package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;
import com.knightforge.view.ChessGameObserver;

public interface ObservableChessGame {
    public void addObserver(ChessGameObserver observer);
}
