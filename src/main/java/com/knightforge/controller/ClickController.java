package com.knightforge.controller;


import com.knightforge.view.ViewComponents.ChessboardComponents.ChessSquareComponent;
import com.knightforge.view.ViewComponents.ChessboardComponents.ChessboardComponent;

public class ClickController {
    private final ChessboardComponent chessboard;

    public ClickController(ChessboardComponent chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessSquareComponent chessSquareComponent) {
        chessboard.handleSquareClick(chessSquareComponent.getChessboardPoint());
    }
}
