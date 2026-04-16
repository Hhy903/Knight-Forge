package com.knightforge.controller;


import com.knightforge.view.ChessComponent;
import com.knightforge.view.ChessboardView;

public class ClickController {
    private final ChessboardView chessboard;

    public ClickController(ChessboardView chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        chessboard.handleSquareClick(chessComponent.getChessboardPoint());
    }
}
