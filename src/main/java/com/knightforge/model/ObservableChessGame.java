package com.knightforge.model;
import com.knightforge.model.ChessPieces.ChessPiece;

public interface ObservableChessGame {
//    public void addObserver(GameObserver observer);
    public abstract ChessPiece[][] getBoardState();
}
