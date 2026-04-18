package com.knightforge.view.ViewComponents.ChessboardComponents;


public class ClickController {
    private final ChessboardComponent chessboard;

    public ClickController(ChessboardComponent chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessSquareComponent chessSquareComponent) {
        chessboard.handleSquareClick(chessSquareComponent.getChessboardPoint());
    }
}
