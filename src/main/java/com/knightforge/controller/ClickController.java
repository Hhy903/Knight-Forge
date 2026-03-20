package com.knightforge.controller;


import com.knightforge.model.ChessComponent;
import com.knightforge.view.Chessboard;

public class ClickController {
    private final Chessboard chessboard;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        chessboard.handleSquareClick(chessComponent.getChessboardPoint());
    }
}
