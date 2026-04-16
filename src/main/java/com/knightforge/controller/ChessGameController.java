package com.knightforge.controller;

import com.knightforge.model.ChessGame;
import com.knightforge.view.ChessGameView;

public class ChessGameController {
    ChessGame chessGameModel;
    ChessGameView chessGameView;

    public ChessGameController (ChessGame chessGameModel) {
        this.chessGameModel = chessGameModel;
        chessGameView = new ChessGameView(this, chessGameModel);
        chessGameView.createView();
        chessGameView.createControls();
    }

    public void showView(){
        chessGameView.setVisible(true);
    }

    public void undoLastMove() {
        chessGameModel.undoLastMove();
    }

    public void loadGameFromFile(String filepath) {
        throw new Error("Implement me!");
    }

    public boolean saveGameToFile(String filepath) {
        throw new Error("Implement me!");
    }
}
